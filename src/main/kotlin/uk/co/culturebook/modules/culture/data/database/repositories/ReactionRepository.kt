package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import uk.co.culturebook.modules.culture.data.database.tables.contribution.ContributionReactions
import uk.co.culturebook.modules.culture.data.database.tables.element.ElementReactions
import uk.co.culturebook.modules.culture.data.models.Reaction
import uk.co.culturebook.modules.database.dbQuery
import java.util.*

object ReactionRepository {
    internal fun rowToElementReaction(resultRow: ResultRow, isMine: Boolean = false): Reaction = Reaction(
        resultRow[ElementReactions.id],
        resultRow[ElementReactions.reaction],
        isMine
    )
    internal fun rowToContributionReaction(resultRow: ResultRow, isMine: Boolean = false): Reaction = Reaction(
        resultRow[ContributionReactions.id],
        resultRow[ContributionReactions.reaction],
        isMine
    )

    // TODO: This is a bit of a hack, but it works for now
    internal suspend fun toggleElementReaction(elementId:UUID, reaction: Reaction, userId: String) = dbQuery {
        val deletedSame = ElementReactions.deleteWhere {
            (ElementReactions.elementId eq elementId) and
            (ElementReactions.user_id eq userId) and
            (ElementReactions.reaction eq reaction.reaction)
        } > 0

        if (deletedSame) return@dbQuery false

        ElementReactions.deleteWhere {
            (ElementReactions.elementId eq elementId) and
            (ElementReactions.user_id eq userId)
        }

        ElementReactions.insertIgnore {
            it[ElementReactions.reaction] = reaction.reaction
            it[user_id] = userId
            it[ElementReactions.elementId] = elementId
        }.resultedValues?.singleOrNull()?.let(::rowToElementReaction) != null
    }

    // TODO: This is a bit of a hack, but it works for now
    internal suspend fun toggleContributionReaction(elementId:UUID, reaction: Reaction, userId: String) = dbQuery {
        val deletedSame = ContributionReactions.deleteWhere {
            (ContributionReactions.contributionId eq elementId) and
            (ContributionReactions.user_id eq userId) and
            (ContributionReactions.reaction eq reaction.reaction)
        }

        if (deletedSame > 0) return@dbQuery false

        ContributionReactions.deleteWhere {
            (ContributionReactions.contributionId eq elementId) and
            (ContributionReactions.user_id eq userId)
        }

        ContributionReactions.insertIgnore {
            it[ContributionReactions.reaction] = reaction.reaction
            it[user_id] = userId
            it[ContributionReactions.contributionId] = elementId
        }.resultedValues?.singleOrNull()?.let(::rowToContributionReaction) != null
    }
}