package uk.co.culturebook.modules.culture.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.authentication.data.database.tables.Users

object Cultures : Table() {
    val id = uuid("culture_id").autoGenerate()
    val name = text("culture_name")
    val lat = double("loc_lat")
    val lon = double("loc_lon")
    val user_id = text("user_id").references(
        Users.userId,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()

    override val primaryKey = PrimaryKey(id)
}