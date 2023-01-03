package uk.co.culturebook.modules.authentication.routes.origin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import uk.co.culturebook.modules.authentication.constants.AuthRoute
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserTokenRepository
import uk.co.culturebook.modules.authentication.data.models.interfaces.AuthState
import uk.co.culturebook.modules.authentication.logic.getUserToken
import uk.co.culturebook.modules.authentication.logic.refreshToken


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
                    respond(HttpStatusCode.OK, refreshState)
                }
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, refreshState)
        }
    }
}