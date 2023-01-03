package uk.co.culturebook.modules.authentication.logic

import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserRepository.exists
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserTokenRepository
import uk.co.culturebook.modules.authentication.data.models.interfaces.AuthState
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.generateAccessJwt
import uk.co.culturebook.modules.utils.generateUUID

suspend fun registerUser(callUser: User): AuthState {
    val decryptedEmail = callUser.email.decodeOAuth()
    val decryptedPassword = callUser.password.decodeOAuth()

    if (decryptedEmail.isNullOrEmpty() || !decryptedEmail.isProperEmail()) {
        return AuthState.Error.InvalidEmail
    }

    if (!decryptedPassword.isProperPassword()) {
        return AuthState.Error.InvalidPassword
    }

    if (decryptedEmail.exists()) {
        return AuthState.Error.DuplicateEmail
    }

    val user = User(
        userId = callUser.userId.generateUUID().toString(),
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

