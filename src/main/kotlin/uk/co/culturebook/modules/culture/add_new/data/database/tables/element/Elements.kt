package uk.co.culturebook.modules.culture.add_new.data.database.tables.element

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Cultures

object Elements : Table() {
    val id = uuid("element_id").autoGenerate()
    val culture_id = uuid("culture_id").references(Cultures.id)
    val name = text("name")
    val type = text("type")
    val loc_lat = double("loc_lat")
    val loc_lon = double("loc_lon")
    val event_start_date = datetime("event_start_date").nullable()
    val event_loc_lat = double("event_loc_lat").nullable()
    val event_loc_lon = double("event_loc_lon").nullable()
    val information = largeText("info")

    override val primaryKey = PrimaryKey(id)
}