package uk.co.culturebook.modules.culture.add_new.data.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Cultures
import uk.co.culturebook.modules.culture.add_new.data.interfaces.CulturesDao
import uk.co.culturebook.modules.culture.add_new.data.models.Culture
import uk.co.culturebook.modules.culture.add_new.data.models.Location
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.modules.database.getDistanceFunction
import uk.co.culturebook.modules.database.rawQuery
import uk.co.culturebook.modules.database.similarityFunction
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
        Cultures.select { Cultures.id eq id }.singleOrNull()?.let(CultureRepository::rowToCulture)
    }

    /** MY_SIMILARITY is a user defined function in [similarityFunction] currently only H2 and Postgres dialects have been defined, define your own dialect when needed
     * */
    override suspend fun getCulturesByName(name: String): List<Culture> = dbQuery {
        val query = if (currentDialect is PostgreSQLDialect) {
            """
            SELECT ${Cultures.id.name}, ${Cultures.name.name}, ${Cultures.lat.name}, ${Cultures.lon.name}, MY_SIMILARITY(${Cultures.name.name}, '$name') as distance
            FROM ${Cultures.tableName}
            WHERE ${Cultures.name.name} % '$name'
            ORDER BY distance DESC""".trimIndent()
        } else {
            """
            SELECT ${Cultures.id.name}, ${Cultures.name.name}, ${Cultures.lat.name}, ${Cultures.lon.name}, MY_SIMILARITY(${Cultures.name.name}, '$name') as distance
            FROM ${Cultures.tableName}
            WHERE MY_SIMILARITY(${Cultures.name.name}, '$name') > 0.8
            ORDER BY distance DESC""".trimIndent()
        }
        rawQuery(query, CultureRepository::resultSetToCultures)?.map { it.first } ?: emptyList()
    }


    /** DISTANCE_IN_KM is a user defined function in [getDistanceFunction] currently only H2 and Postgres dialects have been defined, define your own dialect when needed
     *  TODO - DISTANCE_IN_KM is a potentially expensive operation and we are doing it twice, hence increasing the complexity of the query, in future optimisation we would find a way to use it once.
     * */
    override suspend fun getCulturesByLocation(location: Location, kmLimit: Double): List<Culture> =
        rawQuery(
            """
            SELECT ${Cultures.id.name}, ${Cultures.name.name}, ${Cultures.lat.name}, ${Cultures.lon.name}, DISTANCE_IN_KM(${Cultures.lat.name}, ${Cultures.lon.name}, ${location.latitude}, ${location.longitude}) as distance
            FROM ${Cultures.tableName}
            WHERE DISTANCE_IN_KM(${Cultures.lat.name}, ${Cultures.lon.name}, ${location.latitude}, ${location.longitude}) <= $kmLimit
            ORDER BY distance ASC""".trimIndent(),
            transform = CultureRepository::resultSetToCultures
        )?.map { it.first } ?: emptyList()

    override suspend fun insertCulture(culture: Culture): Culture? = dbQuery {
        val statement = Cultures.insert {
            it[id] = culture.id ?: UUID.randomUUID()
            it[name] = culture.name
            it[lat] = culture.location.latitude
            it[lon] = culture.location.longitude
        }
        statement.resultedValues?.singleOrNull()?.let(CultureRepository::rowToCulture)
    }

    override suspend fun deleteCulture(id: UUID): Boolean = dbQuery {
        Cultures.deleteWhere { Cultures.id eq id } > 0
    }

    override suspend fun updateCulture(culture: Culture): Boolean = dbQuery {
        Cultures.update({ Cultures.id eq culture.id!! }) {
            it[name] = culture.name
            it[lat] = culture.location.latitude
            it[lon] = culture.location.longitude
        } > 0
    }
}