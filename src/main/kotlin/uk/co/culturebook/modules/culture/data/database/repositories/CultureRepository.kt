package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.modules.culture.data.database.tables.BlockedCultures
import uk.co.culturebook.modules.culture.data.database.tables.Cultures
import uk.co.culturebook.modules.culture.data.database.tables.FavouriteCultures
import uk.co.culturebook.modules.culture.data.interfaces.CulturesDao
import uk.co.culturebook.modules.culture.data.models.Culture
import uk.co.culturebook.modules.culture.data.models.Location
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
                location = Location(resultSet.getDouble(Cultures.lat.name), resultSet.getDouble(Cultures.lon.name)),
                favourite = resultSet.getBoolean(FavouriteRepository.Favourite)
            ) to resultSet.getDouble("distance")
        }
        cultures.toList()
    }

    override suspend fun getCulture(id: UUID): Culture? = dbQuery {
        Cultures.select { Cultures.id eq id }.singleOrNull()?.let(CultureRepository::rowToCulture)
    }

    /** MY_SIMILARITY is a user defined function in [similarityFunction] currently only H2 and Postgres dialects have been defined, define your own dialect when needed
     * */
    override suspend fun getCulturesByName(userId: String, name: String): List<Culture> = dbQuery {
        val query = """
            SELECT c.${Cultures.id.name}, 
                    c.${Cultures.name.name}, 
                    c.${Cultures.lat.name}, 
                    c.${Cultures.lon.name}, 
                    fc.${FavouriteCultures.cultureId.name} = c.${Cultures.id.name} as ${FavouriteRepository.Favourite},
                    MY_SIMILARITY(${Cultures.name.name}, '$name') as distance
            FROM ${Cultures.tableName} c
            LEFT JOIN ${BlockedCultures.tableName} bc
                ON bc.${BlockedCultures.cultureId.name} = c.${Cultures.id.name} 
                AND bc."${BlockedCultures.userId.name}" = '$userId'
            LEFT JOIN ${FavouriteCultures.tableName} fc
            ON fc.${FavouriteCultures.cultureId.name} = c.${Cultures.id.name} 
                AND fc."${FavouriteCultures.userId.name}" = '$userId'
            WHERE MY_SIMILARITY(${Cultures.name.name}, '$name') > 0.5
                AND bc.${BlockedCultures.id.name} IS NULL
            ORDER BY distance DESC
            """.trimIndent()
        rawQuery(query, CultureRepository::resultSetToCultures)?.map { it.first } ?: emptyList()
    }


    /** DISTANCE_IN_KM is a user defined function in [getDistanceFunction] currently only H2 and Postgres dialects have been defined, define your own dialect when needed
     *  TODO - DISTANCE_IN_KM is a potentially expensive operation and we are doing it twice, hence increasing the complexity of the query, in future optimisation we would find a way to use it once.
     * */
    override suspend fun getCulturesByLocation(userId: String, location: Location, kmLimit: Double): List<Culture> =
        rawQuery(
            """
            SELECT c.${Cultures.id.name}, 
                    c.${Cultures.name.name}, 
                    c.${Cultures.lat.name}, 
                    c.${Cultures.lon.name}, 
                    fc.${FavouriteCultures.cultureId.name} = c.${Cultures.id.name} as ${FavouriteRepository.Favourite},
                    DISTANCE_IN_KM(${Cultures.lat.name}, ${Cultures.lon.name}, ${location.latitude}, ${location.longitude}) as distance
            FROM ${Cultures.tableName} c
            LEFT JOIN ${BlockedCultures.tableName} bc
                ON bc.${BlockedCultures.cultureId.name} = c.${Cultures.id.name} 
                AND bc."${BlockedCultures.userId.name}" = '$userId'
            LEFT JOIN ${FavouriteCultures.tableName} fc
            ON fc.${FavouriteCultures.cultureId.name} = c.${Cultures.id.name} 
                AND fc."${FavouriteCultures.userId.name}" = '$userId'
            WHERE DISTANCE_IN_KM(${Cultures.lat.name}, ${Cultures.lon.name}, ${location.latitude}, ${location.longitude}) <= $kmLimit
                AND bc.${BlockedCultures.id.name} IS NULL
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