package io.culturebook.modules.authentication.routes.authenticated

import io.culturebook.modules.authentication.constants.AuthRoute
import io.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import io.culturebook.modules.authentication.data.models.interfaces.UserDetailsState
import io.culturebook.modules.authentication.logic.getUserDetails
import io.culturebook.modules.authentication.logic.getUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

internal fun Route.updateTos() {
    post(AuthRoute.User.Tos.route) {
        val isSuccess = UserRepository.updateTos(getUserId())
        val isPrivacySuccess = UserRepository.updatePrivacy(getUserId())

        if (isSuccess && isPrivacySuccess) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
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
