package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.modules.culture.data.database.tables.BlockedContributionComments
import uk.co.culturebook.modules.culture.data.database.tables.BlockedElementComments
import uk.co.culturebook.modules.culture.data.database.tables.contribution.ContributionComments
import uk.co.culturebook.modules.culture.data.database.tables.element.ElementComments
import uk.co.culturebook.modules.culture.data.models.Comment
import uk.co.culturebook.modules.database.dbQuery
import java.util.*

object CommentRepository {
    internal fun rowToElementComment(resultRow: ResultRow, isMine: Boolean = false): Comment = Comment(
        resultRow[ElementComments.id],
        resultRow[ElementComments.comment],
        isMine
    )

    internal fun rowToContributionComment(resultRow: ResultRow, isMine: Boolean = false): Comment = Comment(
        resultRow[ContributionComments.id],
        resultRow[ContributionComments.comment],
        isMine
    )

    internal suspend fun insertElementComment(comment: Comment, elementId: UUID, userId: String) = dbQuery {
        ElementComments.insert {
            it[ElementComments.comment] = comment.comment
            it[ElementComments.elementId] = elementId
            it[user_id] = userId
        }.resultedValues?.singleOrNull()?.let(::rowToElementComment)
    }

    internal suspend fun insertContributionComment(comment: Comment, contributionId: UUID, userId: String) = dbQuery {
        ContributionComments.insert {
            it[ContributionComments.comment] = comment.comment
            it[ContributionComments.contributionId] = contributionId
            it[user_id] = userId
        }.resultedValues?.singleOrNull()?.let(::rowToContributionComment)
    }

    internal suspend fun deleteElementComment(commentId: UUID, userId: String) = dbQuery {
        ElementComments.deleteWhere {
            (ElementComments.id eq commentId) and (user_id eq userId)
        } > 0
    }

    internal suspend fun deleteContributionComment(comment: UUID, userId: String) = dbQuery {
        ContributionComments.deleteWhere {
            (ContributionComments.id eq comment) and (user_id eq userId)
        } > 0
    }

    internal suspend fun getElementComments(elementId: UUID, userId: String?) = dbQuery {
        ElementComments
            .leftJoin(
                BlockedElementComments,
                { ElementComments.id },
                { BlockedElementComments.comment_id },
                { BlockedElementComments.user_id eq userId!! })
            .select {
                (ElementComments.elementId eq elementId) and BlockedElementComments.comment_id.isNull()
            }
            .map { rowToElementComment(it, it[ElementComments.user_id] == userId) }
    }

    internal suspend fun getContributionComments(contributionId: UUID, userId: String?) = dbQuery {
        ContributionComments
            .leftJoin(
                BlockedContributionComments,
                { ContributionComments.id },
                { BlockedContributionComments.comment_id },
                { BlockedContributionComments.user_id eq userId!! })
            .select {
                (ContributionComments.contributionId eq contributionId) and (ContributionComments.user_id eq userId!!)
            }.map { rowToContributionComment(it, it[ContributionComments.user_id] == userId) }
    }

    internal suspend fun blockElementComment(comment: Comment, userId: String) = dbQuery {
        BlockedElementComments.insert {
            it[comment_id] = comment.id
            it[user_id] = userId
        }.resultedValues?.singleOrNull() != null
    }

    internal suspend fun blockContributionComment(comment: Comment, userId: String) = dbQuery {
        BlockedContributionComments.insert {
            it[comment_id] = comment.id
            it[user_id] = userId
        }.resultedValues?.singleOrNull() != null
    }
}