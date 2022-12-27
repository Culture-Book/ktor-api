package sig.g.modules.authentication.logic

import sig.g.modules.authentication.data.UserTokenRepository
import sig.g.modules.authentication.data.models.UserToken
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.generateAccessJwt
import java.time.LocalDateTime

suspend fun refreshToken(userToken: UserToken?): AuthState {
    userToken ?: return AuthState.Error.AuthenticationError
    val dbUserToken = UserTokenRepository.getUserToken(userToken) ?: return AuthState.Error.AuthenticationError
    return if (dbUserToken.expiresAt?.isBefore(LocalDateTime.now()) == true) {
        val newToken = generateUserToken(userId = userToken.userId)
        val isSuccess = UserTokenRepository.updateToken(newToken)

        if (isSuccess) {
            val jwt = generateAccessJwt(newToken.userId, newToken.accessToken, newToken.refreshToken)
                ?: return AuthState.Error.Generic
            AuthState.Success(jwt)
        } else {
            AuthState.Error.DatabaseError
        }
    } else {
        AuthState.Error.AuthenticationError
    }
}