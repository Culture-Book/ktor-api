package io.culturebook.modules.authentication.logic

import io.culturebook.modules.authentication.data.models.User
import io.culturebook.modules.authentication.data.models.database.data_access.UserRepository.exists
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.decodeOAuth

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