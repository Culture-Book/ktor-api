package uk.co.culturebook.modules.culture.data.database.repositories

import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.Constants
import uk.co.culturebook.modules.culture.add_new.client
import uk.co.culturebook.modules.culture.data.database.repositories.CommentRepository.rowToComment
import uk.co.culturebook.modules.culture.data.database.repositories.MediaRepository.rowToMedia
import uk.co.culturebook.modules.culture.data.database.repositories.ReactionRepository.rowToReaction
import uk.co.culturebook.modules.culture.data.database.tables.*
import uk.co.culturebook.modules.culture.data.database.tables.Media
import uk.co.culturebook.modules.culture.data.database.tables.contribution.*
import uk.co.culturebook.modules.culture.data.interfaces.ContributionDao
import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute
import uk.co.culturebook.modules.culture.data.models.*
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.functions.Similarity
import java.util.*

object ContributionRepository : ContributionDao {

    private fun rowToContribution(resultRow: ResultRow): Contribution {
        val location =
            resultRow[Contributions.event_loc_lat]?.let { Location(it, resultRow[Contributions.event_loc_lon]!!) }
        val eventStartDate = resultRow[Contributions.event_start_date]

        return Contribution(
            elementId = resultRow[Contributions.element_id],
            id = resultRow[Contributions.id],
            name = resultRow[Contributions.name],
            type = resultRow[Contributions.type].decodeElementType(),
            location = Location(resultRow[Contributions.loc_lat], resultRow[Contributions.loc_lon]),
            eventType = location?.let { EventType(eventStartDate!!, location) },
            information = resultRow[Contributions.information],
            favourite = resultRow.getOrNull(FavouriteContributions.id) != null
        )
    }

    override suspend fun createBucketForContribution(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String
    ): Boolean {
        val response = client.post(MediaRoute.BucketRoute.getBucket(fileHost)) {
            headers {
                append(Constants.Headers.Authorization, "Bearer $bearer")
                append(Constants.Headers.ApiKey, apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status == HttpStatusCode.OK
    }

    override suspend fun getContribution(id: UUID): Contribution? = dbQuery {
        Contributions.select { Contributions.id eq id }.singleOrNull()
            ?.let(ContributionRepository::rowToContribution)
    }

    override suspend fun getDuplicateContribution(name: String, type: String): List<Contribution> = dbQuery {
        Contributions
            .select {
                (Similarity(Contributions.name, name) greaterEq 0.25) and (Contributions.type eq type)
            }
            .orderBy(Similarity(Contributions.name, name), SortOrder.DESC)
            .map(::rowToContribution)
    }

    override suspend fun uploadMedia(
        parent: String,
        apiKey: String,
        bearer: String,
        fileHost: String,
        files: List<MediaFile>
    ): List<MediaFile> {
        val results = arrayListOf<MediaFile>()
        files.forEach { file ->
            val response = client.post(file.getParentUri(fileHost, parent).toURL()) {
                headers {
                    append(Constants.Headers.Authorization, "Bearer $bearer")
                    append(Constants.Headers.ApiKey, apiKey)
                }
                contentType(ContentType.parse(file.contentType))
                setBody(file.dataStream)
            }

            if (response.status == HttpStatusCode.OK) {
                results += file
            }
        }
        return results
    }

    override suspend fun deleteBucketForContribution(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String
    ): Boolean {
        val response = client.delete(MediaRoute.BucketRoute.getBucket(fileHost, request.id)) {
            headers {
                append(Constants.Headers.Authorization, "Bearer $bearer")
                append(Constants.Headers.ApiKey, apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status == HttpStatusCode.OK
    }

    override suspend fun linkContributions(parentId: UUID, elementIds: List<UUID>): Boolean = dbQuery {
        if (elementIds.isEmpty()) return@dbQuery true
        LinkedContributions.batchInsert(elementIds) {
            set(LinkedContributions.parent_element_id, parentId)
            set(LinkedContributions.child_element_id, it)
        }.isNotEmpty()
    }

    override suspend fun insertContribution(contribution: Contribution): Contribution? = dbQuery {
        val statement = Contributions.insert {
            it[id] = contribution.id
            it[name] = contribution.name
            it[type] = contribution.type.name
            it[element_id] = contribution.elementId
            it[loc_lat] = contribution.location.latitude
            it[loc_lon] = contribution.location.longitude
            it[event_start_date] = if (contribution.eventType != null) contribution.eventType.startDateTime else null
            it[event_loc_lat] = if (contribution.eventType != null) contribution.eventType.location.latitude else null
            it[event_loc_lon] = if (contribution.eventType != null) contribution.eventType.location.longitude else null
            it[information] = contribution.information
        }
        statement.resultedValues?.singleOrNull()
            ?.let(ContributionRepository::rowToContribution)
    }

    override suspend fun deleteContribution(elementId: UUID): Boolean = dbQuery {
        Contributions.deleteWhere { id eq elementId } > 0
    }

    override suspend fun updateContribution(element: Contribution): Boolean = dbQuery {
        Contributions.update({ Contributions.id eq element.id }) {
            it[id] = element.id
            it[name] = element.name
            it[type] = element.type.name
            it[loc_lat] = element.location.latitude
            it[loc_lon] = element.location.longitude
            it[event_start_date] = if (element.eventType != null) element.eventType.startDateTime else null
            it[event_loc_lat] = if (element.eventType != null) element.eventType.location.latitude else null
            it[event_loc_lon] = if (element.eventType != null) element.eventType.location.longitude else null
            it[information] = element.information
        } > 0
    }

    override suspend fun getContributions(
        userId: String,
        elementId: UUID,
        searchString: String,
        types: List<ElementType>,
        page: Int,
        limit: Int
    ): List<Contribution> = dbQuery {
        Contributions
            .leftJoin(
                BlockedContributions,
                { BlockedContributions.contributionId },
                { Contributions.id },
                { BlockedContributions.userId eq userId })
            .leftJoin(
                FavouriteContributions,
                { FavouriteContributions.contributionId },
                { Contributions.id },
                { FavouriteContributions.userId eq userId }
            )
            .select {
                Similarity(Contributions.name, searchString) greaterEq 0.25 and
                        BlockedContributions.id.isNull() and
                        (Contributions.type inList types.map { it.toString() }) and
                        (Contributions.element_id eq elementId)
            }
            .orderBy(Similarity(Contributions.name, searchString), SortOrder.DESC)
            .limit(limit, (page - 1L) * limit)
            .map(::rowToContribution)
    }

    suspend fun getContribution(userId: String, id: UUID) = dbQuery {
        val media = ContributionMedia
            .innerJoin(Media, { Media.id }, { ContributionMedia.mediaId })
            .select { ContributionMedia.contributionId eq id }
            .map(::rowToMedia)

        val comments = ContributionComments
            .innerJoin(Comments, { Comments.id }, { ContributionComments.commentId })
            .select { ContributionComments.contributionId eq id }
            .map {
                val isMine = it[Comments.user_id] == userId
                rowToComment(it, isMine)
            }

        val reactions = ContributionReactions
            .innerJoin(Reactions, { Reactions.id }, { ContributionReactions.reactionId })
            .select { ContributionReactions.contributionId eq id }
            .map {
                val isMine = it[Reactions.user_id] == userId
                rowToReaction(it, isMine)
            }

        Contributions
            .leftJoin(
                BlockedContributions,
                { BlockedContributions.contributionId },
                { Contributions.id },
                { BlockedContributions.userId eq userId })
            .leftJoin(
                FavouriteContributions,
                { FavouriteContributions.contributionId },
                { Contributions.id },
                { FavouriteContributions.userId eq userId }
            )
            .select {
                (Contributions.id eq id) and BlockedContributions.id.isNull()
            }
            .map {
                val contribution = rowToContribution(it)
                contribution.copy(
                    media = media,
                    reactions = reactions,
                    comments = comments
                )
            }
            .singleOrNull()
    }

    override suspend fun getContributions(
        userId: String,
        searchString: String,
        types: List<ElementType>,
        page: Int,
        limit: Int
    ): List<Contribution> = dbQuery {
        Contributions
            .leftJoin(
                BlockedContributions,
                { BlockedContributions.contributionId },
                { Contributions.id },
                { BlockedContributions.userId eq userId })
            .leftJoin(
                FavouriteContributions,
                { FavouriteContributions.contributionId },
                { Contributions.id },
                { FavouriteContributions.userId eq userId }
            )
            .select {
                Similarity(Contributions.name, searchString) greaterEq 0.25 and
                        BlockedContributions.id.isNull() and
                        (Contributions.type inList types.map { it.toString() })
            }
            .orderBy(Similarity(Contributions.name, searchString), SortOrder.DESC)
            .limit(limit, (page - 1L) * limit)
            .map(::rowToContribution)
    }
}