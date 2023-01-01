package sig.g.modules.authentication.logic

import sig.g.modules.authentication.data.data_access.UserTokenRepository
import sig.g.modules.authentication.data.models.UserToken
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.generateAccessJwt
import sig.g.modules.authentication.generateRefreshJwt
import java.time.LocalDateTime

suspend fun refreshToken(userToken: UserToken?): AuthState {
    userToken ?: return AuthState.Error.AuthenticationError

    val newToken = generateUserToken(userId = userToken.userId)
    val isSuccess = UserTokenRepository.updateToken(newToken)

    return if (isSuccess) {
        val jwt = generateAccessJwt(newToken.userId, newToken.accessToken)
            ?: return AuthState.Error.Generic
        val refreshJwt = generateRefreshJwt(newToken.userId, newToken.accessToken)
            ?: return AuthState.Error.Generic
        AuthState.Success(jwt, refreshJwt)
    } else {
        AuthState.Error.DatabaseError
    }
}