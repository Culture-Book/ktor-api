package uk.co.culturebook.modules.culture.data.database.tables.element

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users


object ElementReactions : Table() {
    val id = uuid("id").autoGenerate()
    val elementId = uuid("element_id").references(
        Elements.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val reaction = text("reaction")
    val user_id =
        text("user_id").references(Users.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(id, user_id)
}