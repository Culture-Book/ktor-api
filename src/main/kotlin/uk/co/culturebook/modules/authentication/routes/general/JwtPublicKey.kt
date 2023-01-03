package uk.co.culturebook.modules.authentication.routes.general

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.config.AppConfig
import uk.co.culturebook.config.getProperty
import uk.co.culturebook.modules.authentication.constants.AuthRoute

internal fun Route.jwtPublicKey() {
    get(AuthRoute.JwtPublicKey.route) {
        call.respond(AppConfig.JWTConfig.PublicKey.getProperty())
    }
}
