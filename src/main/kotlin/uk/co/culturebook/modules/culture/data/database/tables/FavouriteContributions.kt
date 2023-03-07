package uk.co.culturebook.modules.culture.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.culture.data.database.tables.contribution.Contributions

object FavouriteContributions : Table() {
    val id = uuid("id").autoGenerate()
    val userId =
        text("userId").references(Users.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val contributionId = uuid("contribution_id").references(
        Contributions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}