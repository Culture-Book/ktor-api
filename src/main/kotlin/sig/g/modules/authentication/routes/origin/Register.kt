package sig.g.modules.authentication.routes.origin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.models.User
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.logic.registerUser

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