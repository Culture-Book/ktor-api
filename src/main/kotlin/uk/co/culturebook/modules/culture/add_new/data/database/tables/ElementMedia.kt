package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ElementMedia: Table() {
    val elementId = uuid("element_id").references(Elements.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val mediaId = uuid("media_id").references(Media.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}