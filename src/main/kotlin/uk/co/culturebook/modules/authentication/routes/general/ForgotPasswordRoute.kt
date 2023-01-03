package uk.co.culturebook.modules.authentication.routes.general

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.constants.AuthRoute
import uk.co.culturebook.modules.authentication.logic.forgotPassword
import uk.co.culturebook.modules.authentication.logic.resetPassword
import uk.co.culturebook.modules.email.data.PasswordReset
import uk.co.culturebook.modules.email.data.PasswordResetRequest

internal fun Route.resetPassword() {
    post(AuthRoute.ResetPassword.route) {
        val passwordReset = call.receive<PasswordReset>()
        if (resetPassword(passwordReset)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}

internal fun Route.forgotPasswordRoute() {
    post(AuthRoute.ForgotPassword.route) {
        val passwordResetRequest = call.receive<PasswordResetRequest>()
        forgotPassword(passwordResetRequest)
        call.respond(HttpStatusCode.OK)
    }
}