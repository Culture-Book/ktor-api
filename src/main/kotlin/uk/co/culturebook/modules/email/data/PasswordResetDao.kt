package uk.co.culturebook.modules.email.data

import java.util.*

sealed interface PasswordResetDao {
    suspend fun insertPasswordReset(passwordResetRequest: PasswordResetRequest): PasswordResetRequest?
    suspend fun deletePasswordReset(userId: String): Boolean
    suspend fun getPasswordReset(userId: String, token: UUID): PasswordResetRequest?
}