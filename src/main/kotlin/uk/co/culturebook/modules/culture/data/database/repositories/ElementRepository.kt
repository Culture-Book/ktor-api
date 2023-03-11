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
import uk.co.culturebook.modules.culture.data.database.tables.BlockedElements
import uk.co.culturebook.modules.culture.data.database.tables.Comments
import uk.co.culturebook.modules.culture.data.database.tables.FavouriteElements
import uk.co.culturebook.modules.culture.data.database.tables.Reactions
import uk.co.culturebook.modules.culture.data.database.tables.element.*
import uk.co.culturebook.modules.culture.data.interfaces.ElementDao
import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute
import uk.co.culturebook.modules.culture.data.models.*
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.functions.Distance
import uk.co.culturebook.modules.database.functions.Similarity
import java.util.*
import uk.co.culturebook.modules.culture.data.database.tables.Media as MediaT

object ElementRepository : ElementDao {

    private fun rowToElement(resultRow: ResultRow): Element {
        val location = resultRow[Elements.event_loc_lat]?.let { Location(it, resultRow[Elements.event_loc_lon]!!) }
        val eventStartDate = resultRow[Elements.event_start_date]

        return Element(
            id = resultRow[Elements.id],
            cultureId = resultRow[Elements.culture_id],
            name = resultRow[Elements.name],
            type = resultRow[Elements.type].decodeElementType(),
            location = Location(resultRow[Elements.loc_lat], resultRow[Elements.loc_lon]),
            eventType = location?.let { EventType(eventStartDate!!, location) },
            information = resultRow[Elements.information],
            favourite = resultRow.getOrNull(FavouriteElements.id) != null
        )
    }

    override suspend fun createBucketForElement(
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

    override suspend fun deleteBucketForElement(
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

    override suspend fun getPreviewElements(
        userId: String,
        location: Location,
        types: List<ElementType>,
        kmLimit: Double,
        page: Int,
        limit: Int
    ): List<Element> = dbQuery {
        Elements
            .leftJoin(
                BlockedElements,
                { BlockedElements.elementId },
                { Elements.id },
                { BlockedElements.userId eq userId })
            .leftJoin(
                FavouriteElements,
                { FavouriteElements.elementId },
                { Elements.id },
                { FavouriteElements.userId eq userId }
            )
            .select {
                (Distance(
                    Elements.loc_lat,
                    Elements.loc_lon,
                    location.latitude,
                    location.longitude
                ) lessEq kmLimit and BlockedElements.id.isNull()) and (Elements.type inList types.map { it.toString() })
            }
            .orderBy(
                Distance(Elements.loc_lat, Elements.loc_lon, location.latitude, location.longitude),
                SortOrder.DESC
            )
            .limit(limit, (page - 1L) * limit)
            .map(::rowToElement)
    }

    override suspend fun getPreviewElements(
        userId: String,
        searchString: String,
        types: List<ElementType>,
        kmLimit: Double,
        page: Int,
        limit: Int
    ): List<Element> = dbQuery {
        Elements
            .leftJoin(
                BlockedElements,
                { BlockedElements.elementId },
                { Elements.id },
                { BlockedElements.userId eq userId })
            .leftJoin(
                FavouriteElements,
                { FavouriteElements.elementId },
                { Elements.id },
                { FavouriteElements.userId eq userId }
            )
            .select {
                (Similarity(
                    Elements.name,
                    searchString
                ) greaterEq 0.25 and BlockedElements.id.isNull()) and (Elements.type inList types.map { it.toString() })
            }
            .orderBy(Similarity(Elements.name, searchString), SortOrder.DESC)
            .limit(limit, (page - 1L) * limit)
            .map(::rowToElement)
    }

    override suspend fun getElement(id: UUID): Element? = dbQuery {
        Elements.select { Elements.id eq id }.singleOrNull()?.let(::rowToElement)
    }

    override suspend fun getElement(userId: String, id: UUID): Element? = dbQuery {
        val media = ElementMedia
            .innerJoin(MediaT, { MediaT.id }, { ElementMedia.mediaId })
            .select { ElementMedia.elementId eq id }
            .map(::rowToMedia)

        val comments = ElementComments
            .innerJoin(Comments, { Comments.id }, { ElementComments.commentId })
            .select { ElementComments.elementId eq id }
            .map {
                val isMine = it[Comments.user_id] == userId
                rowToComment(it, isMine)
            }

        val reactions = ElementReactions
            .innerJoin(Reactions, { Reactions.id }, { ElementReactions.reactionId })
            .select { ElementReactions.elementId eq id }
            .map {
                val isMine = it[Reactions.user_id] == userId
                rowToReaction(it, isMine)
            }

        Elements
            .leftJoin(
                BlockedElements,
                { BlockedElements.elementId },
                { Elements.id },
                { BlockedElements.userId eq userId })
            .leftJoin(
                FavouriteElements,
                { FavouriteElements.elementId },
                { Elements.id },
                { FavouriteElements.userId eq userId }
            )
            .select {
                (Elements.id eq id) and BlockedElements.id.isNull()
            }
            .map {
                val element = rowToElement(it)
                element.copy(
                    media = media,
                    reactions = reactions,
                    comments = comments
                )
            }
            .singleOrNull()
    }

    override suspend fun getDuplicateElement(name: String, type: String): List<Element> = dbQuery {
        Elements
            .select {
                (Similarity(Elements.name, name) greaterEq 0.25) and (Elements.type eq type)
            }
            .orderBy(Similarity(Elements.name, name), SortOrder.DESC)
            .map(::rowToElement)
    }

    override suspend fun uploadMedia(
        apiKey: String,
        bearer: String,
        fileHost: String,
        files: List<MediaFile>
    ): List<MediaFile> {
        val results = arrayListOf<MediaFile>()
        files.forEach { file ->
            val response = client.post(file.getUri(fileHost).toURL()) {
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

    override suspend fun linkElements(parentId: UUID, elementIds: List<UUID>): Boolean = dbQuery {
        if (elementIds.isEmpty()) return@dbQuery true
        LinkedElements.batchInsert(elementIds) {
            set(LinkedElements.parent_element_id, parentId)
            set(LinkedElements.child_element_id, it)
        }.isNotEmpty()
    }

    override suspend fun insertElement(element: Element): Element? = dbQuery {
        val statement = Elements.insert {
            it[id] = element.id
            it[culture_id] = element.cultureId
            it[name] = element.name
            it[type] = element.type.name
            it[loc_lat] = element.location.latitude
            it[loc_lon] = element.location.longitude
            it[event_start_date] = if (element.eventType != null) element.eventType.startDateTime else null
            it[event_loc_lat] = if (element.eventType != null) element.eventType.location.latitude else null
            it[event_loc_lon] = if (element.eventType != null) element.eventType.location.longitude else null
            it[information] = element.information
        }
        statement.resultedValues?.singleOrNull()?.let(::rowToElement)
    }

    override suspend fun deleteElement(elementId: UUID): Boolean = dbQuery {
        Elements.deleteWhere { id eq elementId } > 0
    }

    override suspend fun updateElement(element: Element): Boolean = dbQuery {
        Elements.update({ Elements.id eq element.id }) {
            it[id] = element.id
            it[culture_id] = element.cultureId
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
}