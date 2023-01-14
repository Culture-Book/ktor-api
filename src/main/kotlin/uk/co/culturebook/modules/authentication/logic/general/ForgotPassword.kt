package uk.co.culturebook.modules.authentication.logic.general

import io.ktor.server.config.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import uk.co.culturebook.Constants
import uk.co.culturebook.modules.authentication.data.AuthConfig.emailAccount
import uk.co.culturebook.modules.authentication.data.AuthConfig.emailHost
import uk.co.culturebook.modules.authentication.data.AuthConfig.emailPassword
import uk.co.culturebook.modules.authentication.data.AuthConfig.emailResetExpiry
import uk.co.culturebook.modules.authentication.data.AuthConfig.smtpPort
import uk.co.culturebook.modules.authentication.data.constants.EmailContents
import uk.co.culturebook.modules.authentication.data.database.repositories.PasswordResetRepository
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.models.PasswordReset
import uk.co.culturebook.modules.authentication.data.models.PasswordResetRequest
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.logic.utils.isProperEmail
import uk.co.culturebook.modules.authentication.logic.utils.isProperPassword
import java.time.LocalDateTime
import java.util.*

internal suspend fun resetPassword(config: ApplicationConfig, passwordReset: PasswordReset): Boolean {
    val decryptedUserId = passwordReset.userId.decodeOAuth(config) ?: return false

    if (UserRepository.getUser(decryptedUserId) == null) return false

    val dbPasswordReset =
        PasswordResetRepository.getPasswordReset(decryptedUserId, passwordReset.token) ?: return false

    if (dbPasswordReset.expiresAt!!.isBefore(LocalDateTime.now())) return false

    val decryptedPassword = passwordReset.password.decodeOAuth(config) ?: return false
    if (!decryptedPassword.isProperPassword()) return false

    return UserRepository.updatePassword(decryptedUserId, passwordReset.password).also {
        PasswordResetRepository.deletePasswordReset(dbPasswordReset.userId)
    }
}

internal suspend fun forgotPassword(
    config: ApplicationConfig,
    passwordResetRequest: PasswordResetRequest
): Boolean {
    val decryptedEmail = passwordResetRequest.email?.decodeOAuth(config) ?: return false

    if (!decryptedEmail.isProperEmail()) return false

    val user = UserRepository.getUserByEmail(decryptedEmail) ?: return false

    val passwordResetToken = UUID.randomUUID()
    val passwordExpiryMins = config.emailResetExpiry.toLong() / (1000 * 60)
    val passwordExpiry = LocalDateTime.now().plusMinutes(passwordExpiryMins)
    val forgotPasswordLink = Constants.forgotPasswordAppLink(user.userId, passwordResetToken.toString())

    PasswordResetRepository.deletePasswordReset(user.userId)

    PasswordResetRepository.insertPasswordReset(
        PasswordResetRequest(
            passwordResetToken = passwordResetToken,
            userId = user.userId,
            expiresAt = passwordExpiry
        )
    ) ?: return false

    with(EmailContents) {
        runBlocking {
            //TODO refactor this - implement a queue
            delay(6000L)
            sendEmail(
                subjectContent = ForgotPasswordSubject,
                message = getEmailTemplate(
                    displayName = user.displayName ?: "",
                    appName = AppName,
                    validityInMinutes = passwordExpiryMins.toString(),
                    passwordResetLink = forgotPasswordLink
                ),
                email = user.email,
                host = config.emailHost,
                smtp = config.smtpPort.toInt(),
                account = config.emailAccount,
                password = config.emailPassword
            )
        }
    }
    return true
}