package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object ElementReactions: Table() {
    val elementId = uuid("element_id").references(Elements.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val reactionId = uuid("reaction_id").references(Reactions.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}