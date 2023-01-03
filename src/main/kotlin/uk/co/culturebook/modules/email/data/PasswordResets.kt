package uk.co.culturebook.modules.email.data

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import uk.co.culturebook.modules.authentication.data.models.database.Users

object PasswordResets : Table() {
    private val passwordResetId = integer("passwordResetId").autoIncrement()
    val passwordResetToken = uuid("passwordResetToken")
    val userId = text("userId").references(Users.userId)
    val expiresAt = datetime("expiresAt")

    override val primaryKey: PrimaryKey = PrimaryKey(passwordResetId)
}