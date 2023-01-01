package io.culturebook.modules.authentication.logic

import io.culturebook.modules.authentication.data.models.User
import io.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import io.culturebook.modules.authentication.data.models.database.data_access.UserTokenRepository
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.decodeOAuth
import io.culturebook.modules.authentication.generateAccessJwt

suspend fun login(userCall: User): AuthState {
    val decryptedEmail = userCall.email.decodeOAuth() ?: return AuthState.Error.InvalidEmail
    val user = UserRepository.getUserByEmail(decryptedEmail) ?: return AuthState.Error.Generic
    if (user.password.decodeOAuth() != userCall.password.decodeOAuth()) return AuthState.Error.Generic

    UserTokenRepository.deleteToken(user.userId)

    val userToken = generateUserToken(user.userId)
    val jwt = generateAccessJwt(user.userId, userToken.accessToken) ?: return AuthState.Error.Generic

    UserTokenRepository.insertToken(userToken) ?: return AuthState.Error.Generic

    return AuthState.Success(jwt, userToken.refreshToken!!)
}