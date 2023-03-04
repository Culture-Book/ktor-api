package uk.co.culturebook.modules.culture.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.culture.data.database.tables.element.Elements

object BlockedElements : Table() {
    val id = uuid("id").autoGenerate()
    val userId =
        text("userId").references(Users.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val elementId = uuid("element_id").references(
        Elements.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}