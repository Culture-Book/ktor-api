package sig.g.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.constants.COOKIE_NAME
import sig.g.modules.authentication.data.models.states.AuthState

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(Sessions) {
        cookie<AuthState.Success>(COOKIE_NAME) {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    routing {
        get("/") {
            val session = call.sessions.get<AuthState.Success>()
            if (session != null) {
                call.respondText("Hello ${session.jwt}")
            } else {
                call.respondText("Hello World!")
            }
        }
    }
}
