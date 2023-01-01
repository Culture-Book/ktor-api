package io.culturebook.modules.authentication.routes.origin

import io.culturebook.modules.authentication.constants.AuthRoute
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.logic.registerOrLogin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

internal fun Route.registrationOrLogin() {
    post(AuthRoute.RegisterOrLogin.route) {
        when (val state = registerOrLogin(call.receive())) {
            is AuthState.Success -> call.apply {
                sessions.set(state)
                respond(HttpStatusCode.Created, state)
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, state)
        }
    }
}