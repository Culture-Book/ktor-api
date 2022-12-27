package sig.g.modules.authentication.routes.google

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute

fun Routing.googleAuthentication() {
    authenticate(AuthRoute.GoogleAuth.route) {
        googleLogin()
        googleCallback()
    }
}