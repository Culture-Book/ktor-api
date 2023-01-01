package io.culturebook.modules.authentication.routes.general

import io.culturebook.config.AppConfig
import io.culturebook.config.getProperty
import io.culturebook.modules.authentication.constants.AuthRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

internal fun Route.jwtPublicKey() {
    get(AuthRoute.JwtPublicKey.route) {
        call.respond(AppConfig.JWTConfig.PublicKey.getProperty())
    }
}
