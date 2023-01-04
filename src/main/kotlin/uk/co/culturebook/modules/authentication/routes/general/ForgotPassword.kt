package uk.co.culturebook.modules.authentication.routes.general

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.models.PasswordReset
import uk.co.culturebook.modules.authentication.data.models.PasswordResetRequest
import uk.co.culturebook.modules.authentication.logic.general.forgotPassword
import uk.co.culturebook.modules.authentication.logic.general.resetPassword

internal fun Route.resetPassword(config: ApplicationConfig) {
    post(AuthRoute.ResetPassword.route) {
        val passwordReset = call.receive<PasswordReset>()
        if (resetPassword(
                config,
                passwordReset
            )
        ) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}

internal fun Route.forgotPasswordRoute(config: ApplicationConfig) {
    post(AuthRoute.ForgotPassword.route) {
        val passwordResetRequest = call.receive<PasswordResetRequest>()
        val passwordReset = forgotPassword(config, passwordResetRequest)
        if (passwordReset) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }
}