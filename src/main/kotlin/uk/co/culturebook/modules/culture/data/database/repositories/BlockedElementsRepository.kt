package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.data.models.cultural.BlockedList
import uk.co.culturebook.modules.culture.data.database.tables.BlockedContributions
import uk.co.culturebook.modules.culture.data.database.tables.BlockedCultures
import uk.co.culturebook.modules.culture.data.database.tables.BlockedElements
import uk.co.culturebook.modules.culture.data.database.tables.Cultures
import uk.co.culturebook.modules.culture.data.database.tables.contribution.Contributions
import uk.co.culturebook.modules.culture.data.database.tables.element.Elements
import uk.co.culturebook.modules.culture.data.models.BlockedContribution
import uk.co.culturebook.modules.culture.data.models.BlockedCulture
import uk.co.culturebook.modules.culture.data.models.BlockedElement
import uk.co.culturebook.modules.database.dbQuery
import java.util.*

object BlockedElementsRepository {
    private fun rowToBlockedElement(row: ResultRow, name: String) = BlockedElement(
        row[BlockedElements.elementId],
        name
    )

    private fun rowToBlockedCulture(row: ResultRow, name: String) = BlockedCulture(
        row[BlockedCultures.cultureId],
        name
    )

    private fun rowToBlockedContribution(row: ResultRow, name: String) = BlockedContribution(
        row[BlockedContributions.contributionId],
        name
    )

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

    private suspend fun getBlockedElements(userId: String) = dbQuery {
        BlockedElements
            .leftJoin(
                Elements,
                { elementId },
                { Elements.id },
                { BlockedElements.userId eq userId }
            )
            .slice(BlockedElements.elementId, Elements.name)
            .selectAll()
            .map { rowToBlockedElement(it, it[Elements.name]) }
    }

    private suspend fun getBlockedCultures(userId: String) = dbQuery {
        BlockedCultures.leftJoin(
            Cultures,
            { cultureId },
            { Cultures.id },
            { BlockedCultures.userId eq userId }
        )
            .slice(BlockedCultures.cultureId, Cultures.name)
            .selectAll()
            .map { rowToBlockedCulture(it, it[Cultures.name]) }
    }

    private suspend fun getBlockedContributions(userId: String) = dbQuery {
        BlockedContributions.leftJoin(
            Contributions,
            { contributionId },
            { Contributions.id },
            { BlockedContributions.userId eq userId }
        )
            .slice(BlockedContributions.contributionId, Contributions.name)
            .selectAll()
            .map { rowToBlockedContribution(it, it[Contributions.name]) }
    }

    suspend fun getBlockedLists(userId: String) = dbQuery {
        val elements = getBlockedElements(userId)
        val cultures = getBlockedCultures(userId)
        val contributions = getBlockedContributions(userId)

        BlockedList(elements, contributions, cultures)
    }
}