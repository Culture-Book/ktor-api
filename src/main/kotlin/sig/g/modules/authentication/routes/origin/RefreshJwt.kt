package sig.g.modules.authentication.routes.origin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.models.states.AuthError
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.logic.getUserToken
import sig.g.modules.authentication.logic.refreshToken


internal fun Route.refreshJwt() {
    get(AuthRoute.JwtRefresh.route) {
        val jwtTokens = call.principal<JWTPrincipal>()?.getUserToken()
        val sessionTokens = call.sessions.get(AuthState.AuthSuccess::class)?.getUserToken()
        if (jwtTokens?.userId.isNullOrEmpty() && jwtTokens?.userId != sessionTokens?.userId) {
            call.respond(HttpStatusCode.Unauthorized, AuthError.AuthenticationError)
            return@get
        }

        when (val authState = refreshToken(jwtTokens)) {
            is AuthState.AuthSuccess -> {
                call.apply {
                    sessions.clear<AuthState.AuthSuccess>()
                    sessions.set(authState)
                    respond(HttpStatusCode.OK, authState.jwt)
                }
            }

            is AuthError -> call.respond(HttpStatusCode.BadRequest, authState)
        }
    }
}