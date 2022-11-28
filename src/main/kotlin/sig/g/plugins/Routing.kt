package sig.g.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureRouting() {
    install(AutoHeadResponse)

    routing {
        get("/") {
            val session = call.sessions.get<UserSession>()
            if (session != null) {
                call.respondText("Hello ${session.accessToken}")
            } else {
                call.respondText("Hello World!")
            }
        }
    }
}
