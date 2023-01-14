package uk.co.culturebook.modules.cultural.add_new.location.data.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.modules.cultural.add_new.location.data.database.tables.Cultures
import uk.co.culturebook.modules.cultural.add_new.location.data.interfaces.CulturesDao
import uk.co.culturebook.modules.cultural.add_new.location.data.models.Culture
import uk.co.culturebook.modules.cultural.add_new.location.data.models.Location
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.rawQuery
import uk.co.culturebook.utils.toUUID
import java.sql.ResultSet
import java.util.*

object CultureRepository : CulturesDao {
    private fun rowToCulture(row: ResultRow) = Culture(
        id = row[Cultures.id],
        name = row[Cultures.name],
        location = Location(row[Cultures.lat], row[Cultures.lon])
    )

    private fun resultSetToCultures(resultSet: ResultSet) = resultSet.use {
        val cultures = arrayListOf<Pair<Culture, Double>>()
        while (it.next()) {
            cultures += Culture(
                id = resultSet.getString(Cultures.id.name).toUUID(),
                name = resultSet.getString(Cultures.name.name),
                location = Location(resultSet.getDouble(Cultures.lat.name), resultSet.getDouble(Cultures.lon.name))
            ) to resultSet.getDouble("distance")
        }
        cultures.toList()
    }

    override suspend fun getCulture(id: UUID): Culture? = dbQuery {
        Cultures.select { Cultures.id eq id }.singleOrNull()?.let(::rowToCulture)
    }

    // This only works in PostgresSQL
    override suspend fun getCulturesByLocation(location: Location, kmLimit: Double): List<Culture> =
        rawQuery(
            """
            SELECT ${Cultures.id.name}, ${Cultures.name.name}, ${Cultures.lat.name}, ${Cultures.lon.name}, DISTANCE_IN_KM(${Cultures.lat.name}, ${Cultures.lon.name}, ${location.latitude}, ${location.longitude}) as distance
            FROM ${Cultures.tableName}
            WHERE DISTANCE_IN_KM(${Cultures.lat.name}, ${Cultures.lon.name}, ${location.latitude}, ${location.longitude}) <= $kmLimit
            ORDER BY distance ASC""".trimIndent(),
            transform = ::resultSetToCultures
        )?.map { it.first } ?: emptyList()

    override suspend fun insertCulture(culture: Culture): Culture? = dbQuery {
        val statement = Cultures.insert {
            it[id] = culture.id
            it[name] = culture.name
            it[lat] = culture.location.latitude
            it[lon] = culture.location.longitude
        }
        statement.resultedValues?.singleOrNull()?.let(::rowToCulture)
    }

    override suspend fun deleteCulture(id: UUID): Boolean = dbQuery {
        Cultures.deleteWhere { Cultures.id eq id } > 0
    }

    override suspend fun updateCulture(culture: Culture): Boolean = dbQuery {
        Cultures.update({ Cultures.id eq culture.id }) {
            it[name] = culture.name
            it[lat] = culture.location.latitude
            it[lon] = culture.location.longitude
        } > 0
    }
}