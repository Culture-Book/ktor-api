package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import uk.co.culturebook.modules.culture.data.database.tables.FavouriteContributions
import uk.co.culturebook.modules.culture.data.database.tables.FavouriteCultures
import uk.co.culturebook.modules.culture.data.database.tables.FavouriteElements
import uk.co.culturebook.modules.database.dbQuery
import java.util.*

object FavouriteRepository {

    const val Favourite = "favourite"

    suspend fun favElementExists(userId: String, uuid: UUID) = dbQuery {
        FavouriteElements.select {
            (FavouriteElements.userId eq userId) and (FavouriteElements.elementId eq uuid)
        }.singleOrNull() != null
    }

    suspend fun favContributionExists(userId: String, uuid: UUID) = dbQuery {
        FavouriteContributions.select {
            (FavouriteContributions.userId eq userId) and (FavouriteContributions.contributionId eq uuid)
        }.singleOrNull() != null
    }

    suspend fun favCultureExists(userId: String, uuid: UUID) = dbQuery {
        FavouriteCultures.select {
            (FavouriteCultures.userId eq userId) and (FavouriteCultures.cultureId eq uuid)
        }.singleOrNull() != null
    }

    suspend fun favouriteElement(userId: String, uuid: UUID) = dbQuery {
        FavouriteElements.insert {
            it[FavouriteElements.userId] = userId
            it[elementId] = uuid
        }.insertedCount > 0
    }

    suspend fun favouriteCulture(userId: String, uuid: UUID) = dbQuery {
        FavouriteCultures.insert {
            it[FavouriteCultures.userId] = userId
            it[cultureId] = uuid
        }.insertedCount > 0
    }

    suspend fun favouriteContribution(userId: String, uuid: UUID) = dbQuery {
        FavouriteContributions.insert {
            it[FavouriteContributions.userId] = userId
            it[contributionId] = uuid
        }.insertedCount > 0
    }

    suspend fun unfavouriteElement(userId: String, uuid: UUID) = dbQuery {
        FavouriteElements.deleteWhere {
            (FavouriteElements.userId eq userId) and (elementId eq uuid)
        } > 0
    }

    suspend fun unfavouriteCulture(userId: String, uuid: UUID) = dbQuery {
        FavouriteCultures.deleteWhere {
            (FavouriteCultures.userId eq userId) and (cultureId eq uuid)
        } > 0
    }

    suspend fun unfavouriteContribution(userId: String, uuid: UUID) = dbQuery {
        FavouriteContributions.deleteWhere {
            (FavouriteContributions.userId eq userId) and (contributionId eq uuid)
        } > 0
    }
}