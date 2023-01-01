package io.culturebook.modules.authentication.routes.origin

import io.culturebook.modules.authentication.constants.AuthRoute
import io.culturebook.modules.authentication.data.models.database.data_access.UserTokenRepository
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.logic.getUserToken
import io.culturebook.modules.authentication.logic.refreshToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*


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