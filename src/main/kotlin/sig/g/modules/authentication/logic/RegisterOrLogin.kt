package sig.g.modules.authentication.logic

import sig.g.modules.authentication.data.data_access.UserRepository.exists
import sig.g.modules.authentication.data.models.User
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.decodeOAuth

suspend fun registerOrLogin(callUser: User): AuthState {
    val decryptedEmail = callUser.email.decodeOAuth()

    if (decryptedEmail.isNullOrEmpty() || !decryptedEmail.isProperEmail()) {
        return AuthState.Error.InvalidEmail
    }

    return if (decryptedEmail.exists()) {
        login(callUser)
    } else {
        registerUser(callUser)
    }
}