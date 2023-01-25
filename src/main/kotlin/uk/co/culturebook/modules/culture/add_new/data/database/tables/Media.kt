package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.Table


object Media : Table() {
    val id = uuid("media_id").autoGenerate()
    val uri = text("uri")

    override val primaryKey = PrimaryKey(id)
}