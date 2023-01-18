package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.Table

object ElementMedia: Table() {
    val elementId = uuid("element_id").references(Elements.id)
    val mediaId = uuid("media_id").references(Media.mediaId)
}