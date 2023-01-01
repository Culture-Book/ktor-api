package io.culturebook.modules.authentication.routes.origin

import io.culturebook.modules.authentication.constants.AuthRoute
import io.culturebook.modules.authentication.data.models.User
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.logic.registerUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

internal fun Route.registration() {
    post(AuthRoute.Register.route) {
        val user = call.receive<User>()
        when (val authState = registerUser(user)) {
            is AuthState.Success -> {
                call.apply {
                    sessions.set(authState)
                    respond(HttpStatusCode.Created, authState)
                }
            }

            is AuthState.Error -> call.respond(HttpStatusCode.BadRequest, authState)
        }
    }
}