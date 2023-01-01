package io.culturebook.modules.authentication.logic

import io.culturebook.modules.authentication.data.models.User
import io.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import io.culturebook.modules.authentication.data.models.database.data_access.UserRepository.exists
import io.culturebook.modules.authentication.data.models.database.data_access.UserTokenRepository
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.decodeOAuth
import io.culturebook.modules.authentication.generateAccessJwt
import java.util.*

suspend fun registerUser(callUser: User): AuthState {
    val decryptedEmail = callUser.email.decodeOAuth()
    val decryptedPassword = callUser.password.decodeOAuth()

    if (decryptedEmail.isNullOrEmpty() || !decryptedEmail.isProperEmail()) {
        return AuthState.Error.InvalidEmail
    }

    if (decryptedPassword.isNullOrEmpty()) {
        return AuthState.Error.InvalidPassword
    }

    if (decryptedEmail.exists()) {
        return AuthState.Error.DuplicateEmail
    }

    val user = User(
        userId = callUser.userId.ifBlank { UUID.randomUUID().toString() },
        profileUri = callUser.profileUri,
        displayName = callUser.displayName,
        password = callUser.password,
        email = decryptedEmail,
        verificationStatus = callUser.verificationStatus,
        registrationStatus = callUser.registrationStatus
    )
    val dbUser =
        UserRepository.registerUser(user) ?: return AuthState.Error.DatabaseError

    val userToken = generateUserToken(dbUser.userId)
    val jwt = generateAccessJwt(user.userId, userToken.accessToken) ?: return AuthState.Error.Generic
    UserTokenRepository.insertToken(userToken) ?: return AuthState.Error.Generic

    return AuthState.Success(jwt, userToken.refreshToken!!)
}