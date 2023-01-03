package uk.co.culturebook.modules.authentication.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.data_access.models.GenericResponse
import uk.co.culturebook.modules.authentication.constants.AuthRoute
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import uk.co.culturebook.modules.authentication.data.models.interfaces.UserDetailsState
import uk.co.culturebook.modules.authentication.logic.getUserDetails
import uk.co.culturebook.modules.authentication.logic.getUserId

internal fun Route.updateTos() {
    post(AuthRoute.User.Tos.route) {
        val isSuccess = UserRepository.updateTos(getUserId())
        val isPrivacySuccess = UserRepository.updatePrivacy(getUserId())

        if (isSuccess && isPrivacySuccess) call.respond(HttpStatusCode.OK, GenericResponse(true)) else call.respond(HttpStatusCode.BadRequest)
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
