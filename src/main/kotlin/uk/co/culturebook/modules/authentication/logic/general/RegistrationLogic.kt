package uk.co.culturebook.modules.authentication.logic.general

import io.ktor.server.config.*
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository.exists
import uk.co.culturebook.modules.authentication.data.database.repositories.UserTokenRepository
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.generateAccessJwt
import uk.co.culturebook.modules.authentication.logic.utils.generateUserToken
import uk.co.culturebook.modules.authentication.logic.utils.isProperEmail
import uk.co.culturebook.modules.authentication.logic.utils.isProperPassword
import uk.co.culturebook.utils.toUUIDOrRandom

suspend fun registerUser(config: ApplicationConfig, callUser: User): AuthState {
    val decryptedEmail = callUser.email.decodeOAuth(config)
    val decryptedPassword = callUser.password.decodeOAuth(config)

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
        userId = callUser.userId.toUUIDOrRandom().toString(),
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
    val jwt = generateAccessJwt(config, user.userId, userToken.accessToken) ?: return AuthState.Error.Generic
    UserTokenRepository.insertToken(userToken) ?: return AuthState.Error.Generic

    return AuthState.Success(jwt, userToken.refreshToken!!)
}

