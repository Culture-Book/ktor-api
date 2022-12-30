package sig.g.modules.authentication.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.data_access.UserRepository
import sig.g.modules.authentication.data.models.states.UserDetailsState
import sig.g.modules.authentication.logic.getUserDetails
import sig.g.modules.authentication.logic.getUserId

internal fun Route.updateTos() {
    post(AuthRoute.User.Tos.route) {
        val isSuccess = UserRepository.updateTos(getUserId())
        if (isSuccess) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}

internal fun Route.updatePrivacy() {
    post(AuthRoute.User.Privacy.route) {
        val isSuccess = UserRepository.updatePrivacy(getUserId())
        if (isSuccess) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}

internal fun Route.getUserDetailsRoute() {
    get(AuthRoute.User.route) {
        when (val userState = getUserDetails(getUserId())) {
            is UserDetailsState.Success -> call.respond(HttpStatusCode.OK, userState.user)
            is UserDetailsState.Error -> call.respond(HttpStatusCode.BadRequest, userState)
        }
    }
}
