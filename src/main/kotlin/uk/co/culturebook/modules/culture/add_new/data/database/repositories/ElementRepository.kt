package uk.co.culturebook.modules.culture.add_new.data.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Elements
import uk.co.culturebook.modules.culture.add_new.data.interfaces.ElementDao
import uk.co.culturebook.modules.culture.add_new.data.models.Element
import uk.co.culturebook.modules.culture.add_new.data.models.ElementType
import uk.co.culturebook.modules.culture.add_new.data.models.Location
import uk.co.culturebook.modules.culture.add_new.data.models.decodeElementType
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
            name = resultRow[Elements.name],
            type = resultRow[Elements.type].decodeElementType(location, eventStartDate),
            location = Location(resultRow[Elements.loc_lat], resultRow[Elements.loc_lon]),
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
                name = resultSet.getString(Elements.name.name),
                type = resultSet.getString(Elements.type.name).decodeElementType(eventLocation, eventStartDate),
                location = location,
                information = resultSet.getString(Elements.information.name),
            ) to resultSet.getDouble("distance")
        }
        elements.toList()
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

    override suspend fun insertElement(element: Element): Element? = dbQuery {
        val statement = Elements.insert {
            it[id] = element.id
            it[name] = element.name
            it[type] = element.type.name
            it[loc_lat] = element.location.latitude
            it[loc_lon] = element.location.longitude
            it[event_start_date] = if (element.type is ElementType.Event) element.type.startDateTime else null
            it[event_loc_lat] = if (element.type is ElementType.Event) element.type.startLocation.latitude else null
            it[event_loc_lon] = if (element.type is ElementType.Event) element.type.startLocation.longitude else null
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
            it[name] = element.name
            it[type] = element.type.name
            it[loc_lat] = element.location.latitude
            it[loc_lon] = element.location.longitude
            it[event_start_date] = if (element.type is ElementType.Event) element.type.startDateTime else null
            it[event_loc_lat] = if (element.type is ElementType.Event) element.type.startLocation.latitude else null
            it[event_loc_lon] = if (element.type is ElementType.Event) element.type.startLocation.longitude else null
            it[information] = element.information
        } > 0
    }
}