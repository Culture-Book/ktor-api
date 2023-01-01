package io.culturebook.modules.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.logic.configureJwt
import sig.g.modules.authentication.routes.authenticated.authenticationRoutes
import sig.g.modules.authentication.routes.general.getPrivacy
import sig.g.modules.authentication.routes.general.getTos
import sig.g.modules.authentication.routes.origin.originAuthentication

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