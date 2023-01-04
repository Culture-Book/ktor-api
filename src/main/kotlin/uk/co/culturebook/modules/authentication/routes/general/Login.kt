package uk.co.culturebook.modules.authentication.routes.general

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.logic.general.login

internal fun Route.signIn(config: ApplicationConfig) {
    post(AuthRoute.Login.route) {
        when (val authState = login(config, call.receive())) {
            is AuthState.Success -> {
                call.apply {
                    sessions.set(authState)
                    respond(HttpStatusCode.OK, authState)
                }
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, authState)
        }
    }
}