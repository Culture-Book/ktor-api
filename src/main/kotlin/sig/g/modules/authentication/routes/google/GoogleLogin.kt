package sig.g.modules.authentication.routes.google

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute

internal fun Route.googleLogin() {
    get(AuthRoute.GoogleLogin.route) {
        call.respondRedirect(AuthRoute.GoogleCallback.route)
    }
}