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
import uk.co.culturebook.modules.authentication.logic.general.registerOrLogin

internal fun Route.registrationOrLogin(config: ApplicationConfig) {
    post(AuthRoute.RegisterOrLogin.route) {
        when (val state = registerOrLogin(config, call.receive())) {
            is AuthState.Success -> call.apply {
                sessions.set(state)
                respond(HttpStatusCode.Created, state)
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, state)
        }
    }
}