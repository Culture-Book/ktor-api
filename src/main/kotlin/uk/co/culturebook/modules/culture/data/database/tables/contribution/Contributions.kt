package uk.co.culturebook.modules.culture.data.database.tables.contribution

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.culture.data.database.tables.element.Elements

object Contributions : Table() {
    val id = uuid("contribution_id").autoGenerate()
    val element_id = uuid("element_id").references(
        Elements.id,
        onDelete = ReferenceOption.SET_NULL,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()
    val user_id = text("userId").references(
        Users.userId,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
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