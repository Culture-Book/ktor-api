package uk.co.culturebook.modules.authentication.data.interfaces

import uk.co.culturebook.modules.authentication.data.models.PasswordResetRequest
import java.util.*

interface PasswordResetDao {
    suspend fun insertPasswordReset(passwordResetRequest: PasswordResetRequest): PasswordResetRequest?
    suspend fun deletePasswordReset(userId: String): Boolean
    suspend fun getPasswordReset(userId: String, token: UUID): PasswordResetRequest?
}