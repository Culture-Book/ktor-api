package uk.co.culturebook.modules.culture.data.database.tables.element

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.culture.data.database.tables.Reactions
import uk.co.culturebook.modules.culture.data.database.tables.contribution.Contributions


object ElementReactions : Table() {
    val elementId = uuid("element_id").references(
        Contributions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val reactionId = uuid("reaction_id").references(
        Reactions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}