package sig.g.modules.authentication.logic

import sig.g.modules.authentication.data.UserRepository
import sig.g.modules.authentication.data.UserTokenRepository
import sig.g.modules.authentication.data.models.User
import sig.g.modules.authentication.data.models.states.AuthError
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.decodeOAuth
import sig.g.modules.authentication.generateAccessJwt

suspend fun login(userCall: User): AuthState {
    val decryptedEmail = userCall.email.decodeOAuth() ?: return AuthError.InvalidEmail
    val user = UserRepository.getUserByEmail(decryptedEmail) ?: return AuthError.InvalidArgumentError
    if (user.password.decodeOAuth() != userCall.password.decodeOAuth()) return AuthError.InvalidArgumentError

    UserTokenRepository.deleteToken(user.userId)

    val userToken = generateUserToken(user.userId)
    val jwt = generateAccessJwt(user.userId, userToken.accessToken, userToken.refreshToken)
        ?: return AuthError.InvalidArgumentError

    UserTokenRepository.insertToken(userToken) ?: return AuthError.InvalidArgumentError

    return AuthState.AuthSuccess(jwt)
}