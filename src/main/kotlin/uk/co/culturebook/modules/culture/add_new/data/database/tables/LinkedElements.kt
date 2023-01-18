package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.Table

object LinkedElements: Table() {
    val parent_element_id = uuid("parent_element_id").autoGenerate().references(Elements.id)
    val child_element_id = uuid("child_element_id").autoGenerate().references(Elements.id)
}