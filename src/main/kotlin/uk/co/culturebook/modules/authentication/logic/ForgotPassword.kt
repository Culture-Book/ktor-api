package uk.co.culturebook.modules.authentication.logic

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import uk.co.culturebook.Constants
import uk.co.culturebook.config.AppConfig
import uk.co.culturebook.config.getProperty
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserRepository.exists
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.email.data.EmailContents
import uk.co.culturebook.modules.email.data.PasswordReset
import uk.co.culturebook.modules.email.data.PasswordResetRepository
import uk.co.culturebook.modules.email.data.PasswordResetRequest
import uk.co.culturebook.modules.email.logic.sendEmail
import java.time.LocalDateTime
import java.util.*
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

internal suspend fun resetPassword(passwordReset: PasswordReset): Boolean {
    val decryptedEmail = passwordReset.email.decodeOAuth() ?: return false

    if (!decryptedEmail.isProperEmail()) return false
    if (!decryptedEmail.exists()) return false

    val dbPasswordReset =
        PasswordResetRepository.getPasswordReset(decryptedEmail, passwordReset.token) ?: return false

    if (dbPasswordReset.expiresAt!!.isBefore(LocalDateTime.now())) return false

    val decryptedPassword = passwordReset.password.decodeOAuth() ?: return false
    if (!decryptedPassword.isProperPassword()) return false

    return UserRepository.updatePassword(decryptedEmail, passwordReset.password).also {
        PasswordResetRepository.deletePasswordReset(dbPasswordReset.userId)
    }
}

internal suspend fun forgotPassword(passwordResetRequest: PasswordResetRequest): Boolean {
    val decryptedEmail = passwordResetRequest.email?.decodeOAuth() ?: return false

    if (!decryptedEmail.isProperEmail()) return false

    val user = UserRepository.getUserByEmail(decryptedEmail) ?: return false

    val passwordResetToken = UUID.randomUUID()
    val passwordExpiryMins = AppConfig.EmailConfig.PasswordResetExpiry.getProperty().toLong() / (1000 * 60)
    val passwordExpiry = LocalDateTime.now().plusMinutes(passwordExpiryMins)
    val forgotPasswordLink = Constants.forgotPasswordAppLink(user.email, passwordResetToken.toString())

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
                email = user.email
            )
        }
    }
    return true
}