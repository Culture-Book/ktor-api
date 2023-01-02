package uk.co.culturebook.modules.authentication

import uk.co.culturebook.modules.authentication.constants.AuthRoute
import uk.co.culturebook.modules.authentication.logic.configureJwt
import uk.co.culturebook.modules.authentication.routes.authenticated.authenticationRoutes
import uk.co.culturebook.modules.authentication.routes.general.getPrivacy
import uk.co.culturebook.modules.authentication.routes.general.getTos
import uk.co.culturebook.modules.authentication.routes.origin.originAuthentication
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {
    authentication {
        configureJwt()
    }

    routing {
        route(AuthRoute.AuthRouteVersion.V1.route) {
            originAuthentication()
            authenticationRoutes()
        }
    }

    routing {
        getTos()
        getPrivacy()
    }
}