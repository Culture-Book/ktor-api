package uk.co.culturebook.modules.culture.data.database.tables.contribution

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users

object ContributionComments : Table() {
    val id = uuid("comment_id").autoGenerate()
    val contributionId = uuid("contribution_id").references(
        Contributions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val user_id =
        text("user_id").references(Users.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val comment = text("comment")

    override val primaryKey = PrimaryKey(id)
}