package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.Table

object Cultures : Table() {
    val id = uuid("culture_id").autoGenerate()
    val name = text("culture_name")
    val lat = double("loc_lat")
    val lon = double("loc_lon")
}