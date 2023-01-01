package sig.g.modules.authentication.routes.origin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.data_access.UserTokenRepository
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.logic.getUserToken
import sig.g.modules.authentication.logic.refreshToken


internal fun Route.refreshJwt() {
    post(AuthRoute.JwtRefresh.route) {
        val authState: AuthState.Success = call.receive()
        val userToken = authState.getUserToken()

        if (userToken.userId.isEmpty() && UserTokenRepository.getUserToken(userToken) != null) {
            call.respond(HttpStatusCode.Unauthorized, AuthState.Error.AuthenticationError)
            return@post
        }

        when (val refreshState = refreshToken(userToken)) {
            is AuthState.Success -> {
                call.apply {
                    sessions.clear<AuthState.Success>()
                    sessions.set(refreshState)
                    respond(HttpStatusCode.OK, refreshState.jwt)
                }
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, refreshState)
        }
    }
}