package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import uk.co.culturebook.modules.culture.data.database.tables.BlockedContributions
import uk.co.culturebook.modules.culture.data.database.tables.BlockedCultures
import uk.co.culturebook.modules.culture.data.database.tables.BlockedElements
import uk.co.culturebook.modules.database.dbQuery
import java.util.*

object BlockedElementsRepository {

    suspend fun blockElement(userId: String, uuid: UUID) = dbQuery {
        BlockedElements.insert {
            it[BlockedElements.userId] = userId
            it[elementId] = uuid
        }.insertedCount > 0
    }

    suspend fun blockCulture(userId: String, uuid: UUID) = dbQuery {
        BlockedCultures.insert {
            it[BlockedCultures.userId] = userId
            it[cultureId] = uuid
        }.insertedCount > 0
    }

    suspend fun blockContribution(userId: String, uuid: UUID) = dbQuery {
        BlockedContributions.insert {
            it[BlockedContributions.userId] = userId
            it[contributionId] = uuid
        }.insertedCount > 0
    }

    suspend fun unblockElement(userId: String, uuid: UUID) = dbQuery {
        BlockedElements.deleteWhere {
            (BlockedElements.userId eq userId) and (elementId eq uuid)
        } > 0
    }

    suspend fun unblockCulture(userId: String, uuid: UUID) = dbQuery {
        BlockedCultures.deleteWhere {
            (BlockedCultures.userId eq userId) and (cultureId eq uuid)
        } > 0
    }

    suspend fun unblockContribution(userId: String, uuid: UUID) = dbQuery {
        BlockedContributions.deleteWhere {
            (BlockedContributions.userId eq userId) and (contributionId eq uuid)
        } > 0
    }

}