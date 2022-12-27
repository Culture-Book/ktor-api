package sig.g.modules.authentication.logic

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.UserTokenRepository
import sig.g.modules.authentication.data.models.UserToken
import sig.g.modules.authentication.data.models.states.AuthError
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.generateAccessJwt
import java.time.LocalDateTime

suspend fun refreshToken(userToken: UserToken?): AuthState {
    userToken ?: return AuthError.AuthenticationError
    val dbUserToken = UserTokenRepository.getUserToken(userToken) ?: return AuthError.AuthenticationError
    return if (dbUserToken.expiresAt?.isBefore(LocalDateTime.now()) == true) {
        val newToken = generateUserToken(userId = userToken.userId)
        val isSuccess = UserTokenRepository.updateToken(newToken)

        if (isSuccess) {
            val jwt = generateAccessJwt(newToken.userId, newToken.accessToken, newToken.refreshToken)
                ?: return AuthError.InvalidArgumentError
            AuthState.AuthSuccess(jwt)
        } else {
            AuthError.DatabaseError
        }
    } else {
        AuthError.AuthenticationError
    }
}

fun Route.refreshJwt() {
    get(AuthRoute.JwtRefresh.route) {
        val principal = call.principal<JWTPrincipal>()
        when (val authState = refreshToken(principal?.getUserToken())) {
            is AuthState.AuthSuccess -> {
                call.apply {
                    sessions.set(authState)
                    respond(HttpStatusCode.OK, authState.jwt)
                }
            }

            is AuthError -> call.respond(HttpStatusCode.BadRequest, authState)
        }
    }
}