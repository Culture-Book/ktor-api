package sig.g.modules.authentication.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.models.JwtClaim
import sig.g.modules.authentication.logic.refreshJwt

fun Route.authenticationRoutes() {
    authenticate(AuthRoute.JwtAuth.route) {
        refreshJwt()
    }

    authenticate(AuthRoute.JwtAuth.route) {
        get(AuthRoute.User.route) {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim(JwtClaim.UserId.claim)?.asString() ?: ""
            val user = getUserDetails(userId)

            if (user != null) call.respond(HttpStatusCode.OK, user) else call.respond(HttpStatusCode.Unauthorized)
        }
    }
}