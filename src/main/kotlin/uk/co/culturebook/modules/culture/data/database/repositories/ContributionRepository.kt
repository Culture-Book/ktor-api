package uk.co.culturebook.modules.culture.data.database.repositories

import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import uk.co.culturebook.Constants
import uk.co.culturebook.modules.culture.add_new.client
import uk.co.culturebook.modules.culture.data.database.tables.BlockedContributions
import uk.co.culturebook.modules.culture.data.database.tables.FavouriteContributions
import uk.co.culturebook.modules.culture.data.database.tables.contribution.Contributions
import uk.co.culturebook.modules.culture.data.database.tables.contribution.LinkedContributions
import uk.co.culturebook.modules.culture.data.interfaces.ContributionDao
import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute
import uk.co.culturebook.modules.culture.data.models.*
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.rawQuery
import uk.co.culturebook.utils.toUUID
import java.sql.ResultSet
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
        )
    }

    private fun resultSetToContributions(rs: ResultSet) = rs.use { resultSet ->
        val elements = arrayListOf<Pair<Contribution, Double>>()
        while (resultSet.next()) {
            val latitude = resultSet.getDouble(Contributions.loc_lat.name)
            val longitude = resultSet.getDouble(Contributions.loc_lon.name)
            val location = Location(latitude, longitude)

            val eventStartDate = resultSet.getTimestamp(Contributions.event_start_date.name)?.toLocalDateTime()
            val eventLatitude = resultSet.getDouble(Contributions.event_loc_lat.name)
            val eventLongitude =
                resultSet.getDouble(Contributions.event_loc_lon.name).takeIf { it != 0.0 && latitude != 0.0 }
            val eventLocation = eventLongitude?.let { Location(eventLatitude, eventLongitude) }

            elements += Contribution(
                elementId = resultSet.getString(Contributions.element_id.name).toUUID(),
                id = resultSet.getString(Contributions.id.name).toUUID(),
                name = resultSet.getString(Contributions.name.name),
                type = resultSet.getString(Contributions.type.name).decodeElementType(),
                location = location,
                eventType = eventLocation?.let { EventType(eventStartDate!!, eventLocation) },
                information = resultSet.getString(Contributions.information.name),
                favourite = resultSet.getBoolean(FavouriteRepository.Favourite)
            ) to resultSet.getDouble("distance")
        }
        elements.toList()
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
        val query = if (currentDialect is PostgreSQLDialect) {
            """
            SELECT *, MY_SIMILARITY(${Contributions.name.name}, '$name') as distance
            FROM ${Contributions.tableName} 
            WHERE ${Contributions.name.name} % '$name' AND ${Contributions.type.name} = '$type'
            ORDER BY distance DESC""".trimIndent()
        } else {
            """
            SELECT *, MY_SIMILARITY(${Contributions.name.name}, '$name') as distance
            FROM ${Contributions.tableName}
            WHERE MY_SIMILARITY(${Contributions.name.name}, '$name') > 0.5 AND ${Contributions.type.name} = '$type'
            ORDER BY distance DESC""".trimIndent()
        }
        rawQuery(
            query,
            ContributionRepository::resultSetToContributions
        )?.map { it.first } ?: emptyList()
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
        val typeString = types.joinToString(prefix = "(\'", postfix = "\')", separator = "\',\'")
        val query =
            """
            SELECT c.${Contributions.id.name}, 
                    c.${Contributions.element_id.name},
                    c.${Contributions.name.name},
                    c.${Contributions.type.name},
                    c.${Contributions.loc_lat.name},
                    c.${Contributions.loc_lon.name},
                    c.${Contributions.event_start_date.name},
                    c.${Contributions.event_loc_lat.name},
                    c.${Contributions.event_loc_lon.name},
                    c.${Contributions.information.name},
                    fc.${FavouriteContributions.contributionId.name} = c.${Contributions.id.name} as ${FavouriteRepository.Favourite},
                    MY_SIMILARITY(${Contributions.name.name}, '$searchString') as distance
            FROM ${Contributions.tableName} c
            LEFT JOIN ${BlockedContributions.tableName} bc
            ON bc.${BlockedContributions.contributionId.name} = c.${Contributions.id.name} 
                AND bc."${BlockedContributions.userId.name}" = '$userId'
            LEFT JOIN ${FavouriteContributions.tableName} fc
            ON fc.${FavouriteContributions.contributionId.name} = c.${Contributions.id.name} 
                AND fc."${FavouriteContributions.userId.name}" = '$userId'
            WHERE 
                c.${Contributions.element_id} = '$elementId'
                AND MY_SIMILARITY(${Contributions.name.name}, '$searchString') > 0.5
                AND c.${Contributions.type.name} IN $typeString 
                AND bc.${BlockedContributions.id.name} IS NULL
            ORDER BY distance DESC
            OFFSET ${(page - 1) * limit} ROWS
            FETCH NEXT $limit ROWS ONLY
            """.trimIndent()
        rawQuery(query, ::resultSetToContributions)?.map { it.first } ?: emptyList()
    }

    override suspend fun getContributions(
        userId: String,
        searchString: String,
        types: List<ElementType>,
        page: Int,
        limit: Int
    ): List<Contribution> = dbQuery {
        val typeString = types.joinToString(prefix = "(\'", postfix = "\')", separator = "\',\'")
        val query =
            """
            SELECT c.${Contributions.id.name}, 
                    c.${Contributions.element_id.name},
                    c.${Contributions.name.name},
                    c.${Contributions.type.name},
                    c.${Contributions.loc_lat.name},
                    c.${Contributions.loc_lon.name},
                    c.${Contributions.event_start_date.name},
                    c.${Contributions.event_loc_lat.name},
                    c.${Contributions.event_loc_lon.name},
                    c.${Contributions.information.name},
                    fc.${FavouriteContributions.contributionId.name} = c.${Contributions.id.name} as ${FavouriteRepository.Favourite},
                    MY_SIMILARITY(${Contributions.name.name}, '$searchString') as distance
            FROM ${Contributions.tableName} c
            LEFT JOIN ${BlockedContributions.tableName} bc
            ON bc.${BlockedContributions.contributionId.name} = c.${Contributions.id.name} 
                AND bc."${BlockedContributions.userId.name}" = '$userId'
            LEFT JOIN ${FavouriteContributions.tableName} fc
            ON fc.${FavouriteContributions.contributionId.name} = c.${Contributions.id.name} 
                AND fc."${FavouriteContributions.userId.name}" = '$userId'
            WHERE MY_SIMILARITY(${Contributions.name.name}, '$searchString') > 0.5
                AND c.${Contributions.type.name} IN $typeString 
                AND bc.${BlockedContributions.id.name} IS NULL
            ORDER BY distance DESC
            OFFSET ${(page - 1) * limit} ROWS
            FETCH NEXT $limit ROWS ONLY
            """.trimIndent()
        rawQuery(query, ::resultSetToContributions)?.map { it.first } ?: emptyList()
    }
}