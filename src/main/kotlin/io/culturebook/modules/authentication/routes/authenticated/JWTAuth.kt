package io.culturebook.modules.authentication.routes.authenticated

import io.culturebook.modules.authentication.constants.AuthRoute
import io.culturebook.modules.authentication.routes.origin.refreshJwt
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.authenticationRoutes() {
    refreshJwt()
    authenticate(AuthRoute.JwtAuth.route) {
        getUserDetailsRoute()
        updateTos()
    }
}