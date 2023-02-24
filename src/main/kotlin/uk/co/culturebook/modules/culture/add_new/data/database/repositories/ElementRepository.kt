package uk.co.culturebook.modules.culture.add_new.data.database.repositories

import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import uk.co.culturebook.Constants
import uk.co.culturebook.modules.culture.add_new.client
import uk.co.culturebook.modules.culture.add_new.data.database.tables.element.Elements
import uk.co.culturebook.modules.culture.add_new.data.database.tables.element.LinkedElements
import uk.co.culturebook.modules.culture.add_new.data.interfaces.ElementDao
import uk.co.culturebook.modules.culture.add_new.data.interfaces.external.MediaRoute
import uk.co.culturebook.modules.culture.add_new.data.models.*
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.rawQuery
import uk.co.culturebook.utils.toUUID
import java.sql.ResultSet
import java.util.*

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

            elements += Element(
                id = resultSet.getString(Elements.id.name).toUUID(),
                cultureId = resultSet.getString(Elements.culture_id.name).toUUID(),
                name = resultSet.getString(Elements.name.name),
                type = resultSet.getString(Elements.type.name).decodeElementType(),
                location = location,
                eventType = eventLocation?.let { EventType(eventStartDate!!, eventLocation) },
                information = resultSet.getString(Elements.information.name),
            ) to resultSet.getDouble("distance")
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

    override suspend fun getElement(id: UUID): Element? = dbQuery {
        Elements.select { Elements.id eq id }.singleOrNull()?.let(::rowToElement)
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
            WHERE MY_SIMILARITY(${Elements.name.name}, '$name') > 0.8 AND ${Elements.type.name} = '$type'
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