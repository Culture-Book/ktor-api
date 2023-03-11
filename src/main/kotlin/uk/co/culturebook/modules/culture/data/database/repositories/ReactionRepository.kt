package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.ResultRow
import uk.co.culturebook.modules.culture.data.database.tables.Reactions
import uk.co.culturebook.modules.culture.data.models.Reaction

object ReactionRepository {
    internal fun rowToReaction(resultRow: ResultRow, isMine: Boolean = false): Reaction = Reaction(
        resultRow[Reactions.id],
        resultRow[Reactions.reaction],
        isMine
    )
}