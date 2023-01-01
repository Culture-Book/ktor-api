package sig.g.modules.authentication.routes.authenticated

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.routes.origin.refreshJwt

fun Route.authenticationRoutes() {
    authenticate(AuthRoute.JwtRefreshAuth.route) {
        refreshJwt()
    }

    authenticate(AuthRoute.JwtAuth.route) {
        getUserDetailsRoute()
        updateTos()
        updatePrivacy()
    }
}