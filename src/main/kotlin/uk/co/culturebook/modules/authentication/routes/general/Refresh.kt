package uk.co.culturebook.modules.authentication.routes.general

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import uk.co.culturebook.modules.authentication.data.database.repositories.UserTokenRepository
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.logic.general.refreshToken
import uk.co.culturebook.modules.authentication.logic.utils.getUserToken


internal fun Route.refreshJwt(config: ApplicationConfig) {
    post(AuthRoute.JwtRefresh.route) {
        val authState: AuthState.Success = call.receive()
        val userToken = authState.getUserToken()

        if (userToken.userId.isEmpty() && UserTokenRepository.getUserToken(userToken) != null) {
            call.respond(HttpStatusCode.Unauthorized, AuthState.Error.AuthenticationError)
            return@post
        }

        when (val refreshState = refreshToken(config, userToken)) {
            is AuthState.Success -> {
                call.apply {
                    sessions.clear<AuthState.Success>()
                    sessions.set(refreshState)
                    respond(HttpStatusCode.OK, refreshState)
                }
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, refreshState)
        }
    }
}