package uk.co.culturebook.modules.culture.data.database.repositories

import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import uk.co.culturebook.Constants
import uk.co.culturebook.modules.culture.add_new.client
import uk.co.culturebook.modules.culture.data.database.tables.*
import uk.co.culturebook.modules.culture.data.database.tables.element.*
import uk.co.culturebook.modules.culture.data.interfaces.ElementDao
import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute
import uk.co.culturebook.modules.culture.data.models.*
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.rawQuery
import uk.co.culturebook.utils.toUUID
import uk.co.culturebook.utils.toUri
import java.sql.ResultSet
import java.util.*
import uk.co.culturebook.modules.culture.data.database.tables.Media as MediaT

object ElementRepository : ElementDao {

    private fun rowToMedia(resultRow: ResultRow): Media = Media(
        resultRow[MediaT.id],
        resultRow[MediaT.uri].toUri()!!,
        resultRow[MediaT.contentType]
    )

    private fun rowToComment(resultRow: ResultRow, isMine: Boolean = false): Comment = Comment(
        resultRow[Comments.id],
        resultRow[Comments.comment],
        isMine
    )

    private fun rowToReaction(resultRow: ResultRow, isMine: Boolean = false): Reaction = Reaction(
        resultRow[Reactions.id],
        resultRow[Reactions.reaction],
        isMine
    )

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
        )
    }

    private fun resultSetToElements(rs: ResultSet) = rs.use { resultSet ->
        val elements = arrayListOf<Pair<Element, Double>>()
        while (resultSet.next()) {
            val latitude = resultSet.getDouble(Elements.loc_lat.name)
            val longitude = resultSet.getDouble(Elements.loc_lon.name)
            val location = Location(latitude, longitude)

            val eventStartDate = resultSet.getTimestamp(Elements.event_start_date.name)?.toLocalDateTime()
            val eventLatitude = resultSet.getDouble(Elements.event_loc_lat.name)
            val eventLongitude =
                resultSet.getDouble(Elements.event_loc_lon.name).takeIf { it != 0.0 && latitude != 0.0 }
            val eventLocation = eventLongitude?.let { Location(eventLatitude, eventLongitude) }
            val distance = try {
                resultSet.getDouble("distance")
            } catch (e: Exception) {
                0.0
            }

            elements += Element(
                id = resultSet.getString(Elements.id.name).toUUID(),
                cultureId = resultSet.getString(Elements.culture_id.name).toUUID(),
                name = resultSet.getString(Elements.name.name),
                type = resultSet.getString(Elements.type.name).decodeElementType(),
                location = location,
                eventType = eventLocation?.let { EventType(eventStartDate!!, eventLocation) },
                information = resultSet.getString(Elements.information.name),
                favourite = resultSet.getBoolean(FavouriteRepository.Favourite)
            ) to distance
        }
        elements.toList()
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
        val typeString = types.joinToString(prefix = "(\'", postfix = "\')", separator = "\',\'")
        val query =
            """
            SELECT e.${Elements.id.name}, 
                    e.${Elements.culture_id.name},
                    e.${Elements.name.name},
                    e.${Elements.type.name},
                    e.${Elements.loc_lat.name},
                    e.${Elements.loc_lon.name},
                    e.${Elements.event_start_date.name},
                    e.${Elements.event_loc_lat.name},
                    e.${Elements.event_loc_lon.name},
                    e.${Elements.information.name},
                    fe.${FavouriteElements.elementId.name} = e.${Elements.id.name} as ${FavouriteRepository.Favourite},
                    DISTANCE_IN_KM(${Cultures.lat.name}, ${Cultures.lon.name}, ${location.latitude}, ${location.longitude}) as distance
            FROM ${Elements.tableName} e
            LEFT JOIN ${BlockedElements.tableName} be
            ON be.${BlockedElements.elementId.name} = e.${Elements.id.name} 
                AND be."${BlockedElements.userId.name}" = '$userId'
            LEFT JOIN ${FavouriteElements.tableName} fe
            ON fe.${FavouriteElements.elementId.name} = e.${Elements.id.name} 
                AND fe."${FavouriteElements.userId.name}" = '$userId'
            WHERE DISTANCE_IN_KM(${Elements.loc_lat.name}, ${Elements.loc_lon.name}, ${location.latitude}, ${location.longitude}) <= $kmLimit 
                AND ${Elements.type.name} IN $typeString 
                AND be.${BlockedElements.id.name} IS NULL
            ORDER BY distance DESC
            OFFSET ${(page - 1) * limit} ROWS
            FETCH NEXT $limit ROWS ONLY
            """.trimIndent()
        rawQuery(query, transform = ::resultSetToElements)?.map { it.first } ?: emptyList()
    }

    override suspend fun getPreviewElements(
        userId: String,
        searchString: String,
        types: List<ElementType>,
        kmLimit: Double,
        page: Int,
        limit: Int
    ): List<Element> = dbQuery {
        val typeString = types.joinToString(prefix = "(\'", postfix = "\')", separator = "\',\'")
        val query =
            """
            SELECT e.${Elements.id.name}, 
                    e.${Elements.culture_id.name},
                    e.${Elements.name.name},
                    e.${Elements.type.name},
                    e.${Elements.loc_lat.name},
                    e.${Elements.loc_lon.name},
                    e.${Elements.event_start_date.name},
                    e.${Elements.event_loc_lat.name},
                    e.${Elements.event_loc_lon.name},
                    e.${Elements.information.name},
                    fe.${FavouriteElements.elementId.name} = e.${Elements.id.name} as ${FavouriteRepository.Favourite},
                    MY_SIMILARITY(${Elements.name.name}, '$searchString') as distance
            FROM ${Elements.tableName} e
            LEFT JOIN ${BlockedElements.tableName} be
            ON be.${BlockedElements.elementId.name} = e.${Elements.id.name} 
                AND be."${BlockedElements.userId.name}" = '$userId'
            LEFT JOIN ${FavouriteElements.tableName} fe
            ON fe.${FavouriteElements.elementId.name} = e.${Elements.id.name} 
                AND fe."${FavouriteElements.userId.name}" = '$userId'
            WHERE MY_SIMILARITY(${Elements.name.name}, '$searchString') > 0.5
                AND e.${Elements.type.name} IN $typeString 
                AND be.${BlockedElements.id.name} IS NULL
            ORDER BY distance DESC
            OFFSET ${(page - 1) * limit} ROWS
            FETCH NEXT $limit ROWS ONLY
            """.trimIndent()
        rawQuery(query, ::resultSetToElements)?.map { it.first } ?: emptyList()
    }

    override suspend fun getElement(id: UUID): Element? = dbQuery {
        Elements.select { Elements.id eq id }.singleOrNull()?.let(::rowToElement)
    }

    override suspend fun getElement(userId: String, id: UUID): Element? = dbQuery {
        val mediaQuery = ElementMedia
            .innerJoin(MediaT, { MediaT.id }, { ElementMedia.mediaId })
            .select { ElementMedia.elementId eq id }
        val media = mediaQuery.map(::rowToMedia)

        val commentQuery = ElementComments
            .innerJoin(Comments, { Comments.id }, { ElementComments.commentId })
            .slice(
                Comments.id,
                Comments.comment,
                (Comments.user_id eq userId).alias("isMine")
            )
            .select { ElementComments.elementId eq id }
        val comments = commentQuery.map {
            val isMine = it[Comments.user_id] == userId
            rowToComment(it, isMine)
        }

        val reactionsQuery = ElementReactions
            .innerJoin(Reactions, { Reactions.id }, { ElementReactions.reactionId })
            .slice(
                Reactions.id,
                Reactions.reaction,
                (Reactions.user_id eq userId).alias("isMine")
            )
            .select { ElementReactions.elementId eq id }

        val reactions = reactionsQuery.map {
            val isMine = it[Reactions.user_id] == userId
            rowToReaction(it, isMine)
        }

        Elements
            .leftJoin(
                BlockedElements,
                { BlockedElements.elementId },
                { Elements.id },
                { BlockedElements.userId eq userId })
            .slice(
                FavouriteElements.userId eq userId,
                Elements.id,
                Elements.culture_id,
                Elements.name,
                Elements.type,
                Elements.loc_lat,
                Elements.loc_lon,
                Elements.event_start_date,
                Elements.event_loc_lat,
                Elements.event_loc_lon,
                Elements.information
            )
            .select {
                (Elements.id eq id) and BlockedElements.id.isNull()
            }
            .map {
                val element = rowToElement(it)
                val favourite = it.getOrNull(FavouriteElements.userId eq userId) ?: false
                element.copy(
                    media = media,
                    reactions = reactions,
                    comments = comments,
                    favourite = favourite
                )
            }
            .singleOrNull()
    }

    override suspend fun getDuplicateElement(name: String, type: String): List<Element> = dbQuery {
        val query = if (currentDialect is PostgreSQLDialect) {
            """
            SELECT *, MY_SIMILARITY(${Elements.name.name}, '$name') as distance
            FROM ${Elements.tableName} 
            WHERE ${Elements.name.name} % '$name' AND ${Elements.type.name} = '$type'
            ORDER BY distance DESC""".trimIndent()
        } else {
            """
            SELECT *, MY_SIMILARITY(${Elements.name.name}, '$name') as distance
            FROM ${Elements.tableName}
            WHERE MY_SIMILARITY(${Elements.name.name}, '$name') > 0.5 AND ${Elements.type.name} = '$type'
            ORDER BY distance DESC""".trimIndent()
        }
        rawQuery(query, ::resultSetToElements)?.map { it.first } ?: emptyList()
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