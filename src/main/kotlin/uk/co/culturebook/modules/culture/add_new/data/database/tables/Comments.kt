package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users


object Comments: Table() {
    val id = uuid("comment_id").autoGenerate()
    val user_id = text("user_id").references(Users.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val comment = text("comment")

    override val primaryKey = PrimaryKey(id)
}