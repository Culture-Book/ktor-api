package sig.g.modules.authentication.routes.origin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.logic.getUserToken
import sig.g.modules.authentication.logic.refreshToken


internal fun Route.refreshJwt() {
    get(AuthRoute.JwtRefresh.route) {
        val jwtTokens = call.principal<JWTPrincipal>()?.getUserToken()
        val sessionTokens = call.sessions.get(AuthState.Success::class)?.getUserToken()

        if (jwtTokens?.userId.isNullOrEmpty() && jwtTokens?.userId != sessionTokens?.userId) {
            call.respond(HttpStatusCode.Unauthorized, AuthState.Error.AuthenticationError)
            return@get
        }

        when (val authState = refreshToken(jwtTokens)) {
            is AuthState.Success -> {
                call.apply {
                    sessions.clear<AuthState.Success>()
                    sessions.set(authState)
                    respond(HttpStatusCode.OK, authState.jwt)
                }
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, authState)
        }
    }
}