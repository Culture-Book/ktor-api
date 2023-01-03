package uk.co.culturebook.modules.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.constants.AuthRoute
import uk.co.culturebook.modules.authentication.logic.configureJwt
import uk.co.culturebook.modules.authentication.routes.authenticated.authenticationRoutes
import uk.co.culturebook.modules.authentication.routes.general.getAssetLinks
import uk.co.culturebook.modules.authentication.routes.general.getPrivacy
import uk.co.culturebook.modules.authentication.routes.general.getTos
import uk.co.culturebook.modules.authentication.routes.origin.originAuthentication

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
        getAssetLinks()
    }
}