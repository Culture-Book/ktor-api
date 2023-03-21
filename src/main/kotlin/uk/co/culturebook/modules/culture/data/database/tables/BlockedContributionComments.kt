package uk.co.culturebook.modules.culture.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.culture.data.database.tables.contribution.ContributionComments


object BlockedContributionComments : Table() {
    val id = uuid("id").autoGenerate()
    val user_id =
        text("user_id").references(Users.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val comment_id =
        uuid("comment_id").references(ContributionComments.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(id)
}