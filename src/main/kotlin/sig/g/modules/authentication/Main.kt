package sig.g.modules.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.logic.configureGoogleSignIn
import sig.g.modules.authentication.logic.configureJwt
import sig.g.modules.authentication.routes.authenticated.authenticationRoutes
import sig.g.modules.authentication.routes.google.googleAuthentication
import sig.g.modules.authentication.routes.origin.originAuthentication

fun Application.configureSecurity() {
    authentication {
        configureGoogleSignIn()
        configureJwt()
    }

    routing {
        route(AuthRoute.AuthRouteVersion.V1.route) {
            originAuthentication()
            googleAuthentication()
            authenticationRoutes()
        }
    }
}