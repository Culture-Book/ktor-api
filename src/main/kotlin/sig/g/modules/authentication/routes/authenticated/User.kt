package sig.g.modules.authentication.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.UserRepository
import sig.g.modules.authentication.data.models.JwtClaim
import sig.g.modules.authentication.data.models.User
import sig.g.modules.authentication.logic.getUserDetails
import sig.g.modules.authentication.logic.getUserId

internal fun Route.updateTos() {
    post(AuthRoute.User.Tos.route){
        val isSuccess = UserRepository.updateTos(getUserId())
        if (isSuccess) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}

internal fun Route.updatePrivacy() {
    post(AuthRoute.User.Privacy.route){
        val isSuccess = UserRepository.updatePrivacy(getUserId())
        if (isSuccess) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}

internal fun Route.getUserDetailsRoute() {
    get(AuthRoute.User.route) {
        val user = getUserDetails(getUserId())
        if (user != null) call.respond(HttpStatusCode.OK, user) else call.respond(HttpStatusCode.Unauthorized)
    }
}
