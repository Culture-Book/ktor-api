package uk.co.culturebook.modules.culture.data.database.tables

import org.jetbrains.exposed.sql.Table


object Media : Table() {
    val id = uuid("media_id").autoGenerate()
    val uri = text("uri")
    val contentType = text("content_type")

    override val primaryKey = PrimaryKey(id)
}