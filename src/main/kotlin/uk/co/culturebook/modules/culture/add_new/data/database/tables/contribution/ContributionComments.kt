package uk.co.culturebook.modules.culture.add_new.data.database.tables.contribution

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Comments

object ContributionComments : Table() {
    val contributionId = uuid("contribution_id").references(
        Contributions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val commentId = uuid("comment_id").references(
        Comments.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}