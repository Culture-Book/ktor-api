package sig.g.modules.authentication.routes.origin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.data.models.states.AuthError
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.logic.login
import sig.g.modules.authentication.routes.AuthRoute

internal fun Route.signIn() {
    post(AuthRoute.Login.route) {
        when (val authState = login(call.receive())) {
            is AuthState.AuthSuccess -> {
                call.apply {
                    sessions.set(authState.userSession)
                    respond(HttpStatusCode.Created, authState)
                }
            }

            is AuthError -> call.respond(HttpStatusCode.BadRequest, authState)
        }
    }
}