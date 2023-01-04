package uk.co.culturebook.modules.authentication.logic.general

import io.ktor.server.config.*
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.database.repositories.UserTokenRepository
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.generateAccessJwt
import uk.co.culturebook.modules.authentication.logic.utils.generateUserToken

suspend fun login(config: ApplicationConfig, userCall: User): AuthState {
    val decryptedEmail = userCall.email.decodeOAuth(config) ?: return AuthState.Error.InvalidEmail
    val user = UserRepository.getUserByEmail(decryptedEmail) ?: return AuthState.Error.Generic
    if (user.password.decodeOAuth(config) != userCall.password.decodeOAuth(config)) return AuthState.Error.Generic

    UserTokenRepository.deleteToken(user.userId)

    val userToken = generateUserToken(user.userId)
    val jwt = generateAccessJwt(config, user.userId, userToken.accessToken) ?: return AuthState.Error.Generic

    UserTokenRepository.insertToken(userToken) ?: return AuthState.Error.Generic

    return AuthState.Success(jwt, userToken.refreshToken!!)
}