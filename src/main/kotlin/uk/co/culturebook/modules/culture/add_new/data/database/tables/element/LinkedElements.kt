package uk.co.culturebook.modules.culture.add_new.data.database.tables.element

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.culture.add_new.data.database.tables.contribution.Contributions

object LinkedElements : Table() {
    val parent_element_id = uuid("parent_element_id").autoGenerate()
        .references(Contributions.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val child_element_id = uuid("child_element_id").autoGenerate()
        .references(Contributions.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}