package io.culturebook.modules.authentication.routes.origin

import io.culturebook.modules.authentication.constants.AuthRoute
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.logic.login
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

internal fun Route.signIn() {
    post(AuthRoute.Login.route) {
        when (val authState = login(call.receive())) {
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