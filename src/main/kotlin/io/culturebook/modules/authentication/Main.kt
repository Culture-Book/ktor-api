package io.culturebook.modules.authentication

import io.culturebook.modules.authentication.constants.AuthRoute
import io.culturebook.modules.authentication.logic.configureJwt
import io.culturebook.modules.authentication.routes.authenticated.authenticationRoutes
import io.culturebook.modules.authentication.routes.general.getPrivacy
import io.culturebook.modules.authentication.routes.general.getTos
import io.culturebook.modules.authentication.routes.origin.originAuthentication
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