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
import uk.co.culturebook.modules.database.functions.Distance
import uk.co.culturebook.modules.database.functions.Similarity
import java.util.*

object CultureRepository : CulturesDao {
    private fun rowToCulture(row: ResultRow) = Culture(
        id = row[Cultures.id],
        name = row[Cultures.name],
        location = Location(row[Cultures.lat], row[Cultures.lon]),
        favourite = row.getOrNull(FavouriteCultures.id) != null
    )

    override suspend fun getCulture(id: UUID): Culture? = dbQuery {
        Cultures.select { Cultures.id eq id }.singleOrNull()?.let(CultureRepository::rowToCulture)
    }

    override suspend fun getCulturesByName(userId: String, name: String): List<Culture> = dbQuery {
        Cultures
            .leftJoin(
                BlockedCultures,
                { BlockedCultures.cultureId },
                { Cultures.id },
                { BlockedCultures.userId eq userId })
            .leftJoin(
                FavouriteCultures,
                { FavouriteCultures.cultureId },
                { Cultures.id },
                { FavouriteCultures.userId eq userId }
            )
            .select {
                Similarity(Cultures.name, name) greaterEq 0.25 and BlockedCultures.id.isNull()
            }
            .orderBy(Similarity(Cultures.name, name), SortOrder.DESC)
            .map(::rowToCulture)
    }

    override suspend fun getCulturesByLocation(userId: String, location: Location, kmLimit: Double): List<Culture> =
        dbQuery {
            Cultures
                .leftJoin(
                    BlockedCultures,
                    { BlockedCultures.cultureId },
                    { Cultures.id },
                    { BlockedCultures.userId eq userId })
                .leftJoin(
                    FavouriteCultures,
                    { FavouriteCultures.cultureId },
                    { Cultures.id },
                    { FavouriteCultures.userId eq userId }
                )
                .select {
                    (Distance(
                        Cultures.lat,
                        Cultures.lon,
                        location.latitude,
                        location.longitude
                    ) lessEq kmLimit and BlockedCultures.id.isNull())
                }
                .orderBy(Distance(Cultures.lat, Cultures.lon, location.latitude, location.longitude), SortOrder.DESC)
                .map(::rowToCulture)
        }

    override suspend fun insertCulture(culture: Culture, userId: String): Culture? = dbQuery {
        val statement = Cultures.insert {
            it[id] = culture.id ?: UUID.randomUUID()
            it[name] = culture.name
            it[lat] = culture.location.latitude
            it[lon] = culture.location.longitude
            it[user_id] = userId
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

    override suspend fun getUserCultures(userId: String): List<Culture> = dbQuery {
        Cultures
            .select { Cultures.user_id eq userId }
            .map(::rowToCulture)
    }

    override suspend fun getFavouriteCultures(userId: String): List<Culture> = dbQuery {
        Cultures
            .innerJoin(FavouriteCultures, { Cultures.id }, { FavouriteCultures.cultureId })
            .select { FavouriteCultures.userId eq userId }
            .map(::rowToCulture)
    }
}