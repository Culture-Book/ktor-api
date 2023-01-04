package uk.co.culturebook.modules.authentication.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object PasswordResets : Table() {
    private val passwordResetId = integer("passwordResetId").autoIncrement()
    val passwordResetToken = uuid("passwordResetToken")
    val userId = text("userId").references(Users.userId)
    val expiresAt = datetime("expiresAt")

    override val primaryKey: PrimaryKey = PrimaryKey(passwordResetId)
}