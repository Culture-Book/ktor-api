package uk.co.culturebook.modules.authentication.logic.general

import io.ktor.server.config.*
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.models.PasswordUpdateRequest
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.logic.utils.isProperPassword

internal suspend fun updatePassword(
    config: ApplicationConfig,
    passwordUpdate: PasswordUpdateRequest,
    userId: String
): Boolean {
    // try to decode the passwords, even if we don't use them. If they can't be decoded, they are probably from a different client.
    val oldPassword = passwordUpdate.oldPassword.decodeOAuth(config) ?: return false
    val decodedNewPassword = passwordUpdate.newPassword.decodeOAuth(config) ?: return false

    if (!decodedNewPassword.isProperPassword()) return false

    return UserRepository.updatePassword(userId, oldPassword, passwordUpdate.newPassword, config)
}