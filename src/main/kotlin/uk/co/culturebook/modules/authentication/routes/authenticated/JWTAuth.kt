package uk.co.culturebook.modules.authentication.routes.authenticated

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.constants.AuthRoute
import uk.co.culturebook.modules.authentication.routes.origin.refreshJwt

fun Route.authenticationRoutes() {
    refreshJwt()
    authenticate(AuthRoute.JwtAuth.route) {
        getUserDetailsRoute()
        updateTos()
    }
}