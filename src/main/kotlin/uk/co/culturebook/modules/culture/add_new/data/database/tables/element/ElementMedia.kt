package uk.co.culturebook.modules.culture.add_new.data.database.tables.element

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Media
import uk.co.culturebook.modules.culture.add_new.data.database.tables.contribution.Contributions

object ElementMedia : Table() {
    val elementId = uuid("element_id").references(
        Contributions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val mediaId =
        uuid("media_id").references(Media.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}