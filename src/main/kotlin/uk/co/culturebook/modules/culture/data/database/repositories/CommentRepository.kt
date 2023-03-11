package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.ResultRow
import uk.co.culturebook.modules.culture.data.database.tables.Comments
import uk.co.culturebook.modules.culture.data.models.Comment

object CommentRepository {
    internal fun rowToComment(resultRow: ResultRow, isMine: Boolean = false): Comment = Comment(
        resultRow[Comments.id],
        resultRow[Comments.comment],
        isMine
    )
}