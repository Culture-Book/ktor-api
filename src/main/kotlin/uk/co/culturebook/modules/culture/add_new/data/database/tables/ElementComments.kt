package uk.co.culturebook.modules.culture.add_new.data.database.tables

import org.jetbrains.exposed.sql.Table

object ElementComments: Table() {
    val elementId = uuid("element_id").references(Elements.id)
    val commentId = uuid("comment_id").references(Comments.commentId)
}