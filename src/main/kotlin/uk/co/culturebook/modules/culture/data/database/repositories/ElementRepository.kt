package uk.co.culturebook.modules.culture.data.database.repositories

import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.Constants
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.authentication.data.enums.VerificationStatus
import uk.co.culturebook.modules.culture.add_new.client
import uk.co.culturebook.modules.culture.data.database.repositories.MediaRepository.rowToMedia
import uk.co.culturebook.modules.culture.data.database.tables.BlockedElements
import uk.co.culturebook.modules.culture.data.database.tables.FavouriteElements
import uk.co.culturebook.modules.culture.data.database.tables.element.ElementMedia
import uk.co.culturebook.modules.culture.data.database.tables.element.ElementReactions
import uk.co.culturebook.modules.culture.data.database.tables.element.Elements
import uk.co.culturebook.modules.culture.data.database.tables.element.LinkedElements
import uk.co.culturebook.modules.culture.data.interfaces.ElementDao
import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute
import uk.co.culturebook.modules.culture.data.models.*
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.functions.Distance
import uk.co.culturebook.modules.database.functions.Similarity
import java.util.*
import uk.co.culturebook.modules.culture.data.database.tables.Media as MediaT

object ElementRepository : ElementDao {

    private fun rowToElement(resultRow: ResultRow, minify: Boolean = false): Element {
        val location = resultRow[Elements.event_loc_lat]?.let { Location(it, resultRow[Elements.event_loc_lon]!!) }
        val eventStartDate = resultRow[Elements.event_start_date]
        val information = if (minify) {
            resultRow[Elements.information].substring(0, minOf(40, resultRow[Elements.information].length))
        } else {
            resultRow[Elements.information]
        }

        return Element(
            id = resultRow[Elements.id],
            cultureId = resultRow[Elements.culture_id],
            name = resultRow[Elements.name],
            type = resultRow[Elements.type].decodeElementType(),
            location = Location(resultRow[Elements.loc_lat], resultRow[Elements.loc_lon]),
            eventType = location?.let { EventType(eventStartDate!!, location) },
            information = information,
            favourite = resultRow.getOrNull(FavouriteElements.id) != null,
            isVerified = resultRow.getOrNull(Users.verificationStatus) == VerificationStatus.Verified.ordinal
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
        val emptyResponse = client.post(MediaRoute.BucketRoute.emptyBucket(fileHost, request.id)) {
            headers {
                append(Constants.Headers.Authorization, "Bearer $bearer")
                append(Constants.Headers.ApiKey, apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.status == HttpStatusCode.OK
        if (!emptyResponse) return false
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
            .map { rowToElement(it, true) }
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
            .leftJoin(
                Users,
                { Users.userId },
                { Elements.user_id },
            )
            .select {
                (Similarity(
                    Elements.name,
                    searchString
                ) greaterEq 0.25 and BlockedElements.id.isNull()) and (Elements.type inList types.map { it.toString() })
            }
            .orderBy(Similarity(Elements.name, searchString), SortOrder.DESC)
            .limit(limit, (page - 1L) * limit)
            .map { rowToElement(it, true) }
    }

    override suspend fun getUserElements(
        userId: String,
        types: List<ElementType>,
        kmLimit: Double,
        page: Int,
        limit: Int
    ): List<Element> = dbQuery {
        Elements
            .select { Elements.user_id eq userId }
            .limit(limit, (page - 1L) * limit)
            .map { rowToElement(it, true) }
    }

    override suspend fun getFavouriteElements(
        userId: String,
        types: List<ElementType>,
        kmLimit: Double,
        page: Int,
        limit: Int
    ): List<Element> = dbQuery {
        Elements
            .innerJoin(
                FavouriteElements,
                { FavouriteElements.elementId },
                { Elements.id },
                { FavouriteElements.userId eq userId }
            )
            .leftJoin(
                BlockedElements,
                { BlockedElements.elementId },
                { Elements.id },
                { BlockedElements.userId eq userId })
            .select { (Elements.type inList types.map { it.toString() }) and BlockedElements.id.isNull() }
            .limit(limit, (page - 1L) * limit)
            .map { rowToElement(it, true) }
    }

    override suspend fun getElement(id: UUID): Element? = dbQuery {
        Elements.select { Elements.id eq id }.singleOrNull()?.let(::rowToElement)
    }

    override suspend fun getElement(userId: String, id: UUID): Element? = dbQuery {
        val media = ElementMedia
            .innerJoin(MediaT, { MediaT.id }, { ElementMedia.mediaId })
            .select { ElementMedia.elementId eq id }
            .map(::rowToMedia)

        val reactions = ElementReactions
            .select { ElementReactions.elementId eq id }
            .map {
                val isMine = it[ElementReactions.user_id] == userId
                ReactionRepository.rowToElementReaction(it, isMine)
            }

        val linkedElements = LinkedElements
            .select { LinkedElements.parent_element_id eq id }
            .map { it[LinkedElements.child_element_id] }

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
            .leftJoin(
                Users,
                { Users.userId },
                { Elements.user_id },
            )
            .select {
                (Elements.id eq id) and BlockedElements.id.isNull()
            }
            .map {
                val element = rowToElement(it)
                element.copy(
                    media = media,
                    reactions = reactions,
                    linkElements = linkedElements,
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

    override suspend fun insertElement(element: Element, userId: String): Element? = dbQuery {
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
            it[user_id] = userId
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