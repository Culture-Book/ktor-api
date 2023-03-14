package uk.co.culturebook.modules.culture.data.database.tables.element

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object LinkedElements : Table() {
    val parent_element_id = uuid("parent_element_id").autoGenerate()
        .references(Elements.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val child_element_id = uuid("child_element_id").autoGenerate()
        .references(Elements.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}