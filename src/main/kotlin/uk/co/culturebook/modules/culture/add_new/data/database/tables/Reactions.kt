package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users


object Reactions: Table() {
    val reactionId = uuid("reaction_id").autoGenerate()
    val user_id = text("user_id").references(Users.userId)
    val reaction = text("reaction")
}