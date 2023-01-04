package uk.co.culturebook.modules.authentication.logic.general

import io.ktor.server.config.*
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository.exists
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.logic.utils.isProperEmail

suspend fun registerOrLogin(config: ApplicationConfig, callUser: User): AuthState {
    val decryptedEmail = callUser.email.decodeOAuth(config)

    if (decryptedEmail.isNullOrEmpty() || !decryptedEmail.isProperEmail()) {
        return AuthState.Error.InvalidEmail
    }

    return if (decryptedEmail.exists()) {
        login(config, callUser)
    } else {
        registerUser(config, callUser)
    }
}