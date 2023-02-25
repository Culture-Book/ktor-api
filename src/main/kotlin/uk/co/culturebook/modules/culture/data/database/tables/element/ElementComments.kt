package uk.co.culturebook.modules.culture.data.database.tables.element

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.culture.data.database.tables.Comments
import uk.co.culturebook.modules.culture.data.database.tables.contribution.Contributions

object ElementComments : Table() {
    val elementId = uuid("element_id").references(
        Contributions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val commentId = uuid("comment_id").references(
        Comments.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}