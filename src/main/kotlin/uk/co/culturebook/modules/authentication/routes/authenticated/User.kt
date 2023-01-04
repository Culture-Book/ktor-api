package uk.co.culturebook.modules.authentication.routes.authenticated

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.AuthConfig.privacyDate
import uk.co.culturebook.modules.authentication.data.AuthConfig.toSDate
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.interfaces.UserDetailsState
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserDetails
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserId
import java.time.LocalDateTime

internal fun Route.updateTos() {
    post(AuthRoute.User.Tos.route) {
        val isSuccess = UserRepository.updateTos(getUserId())
        val isPrivacySuccess = UserRepository.updatePrivacy(getUserId())

        if (isSuccess && isPrivacySuccess) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}

internal fun Route.getUserDetailsRoute(config: ApplicationConfig) {
    get(AuthRoute.User.route) {
        val tosDate = LocalDateTime.parse(config.toSDate)
        val privacyDate = LocalDateTime.parse(config.privacyDate)

        when (val userState = getUserDetails(getUserId(), tosDate, privacyDate)) {
            is UserDetailsState.Success -> call.respond(HttpStatusCode.OK, userState.user)
            is UserDetailsState.Error -> call.respond(HttpStatusCode.BadRequest, userState)
        }
    }
}
