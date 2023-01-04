package uk.co.culturebook.modules.authentication.routes.general

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.AuthConfig.publicKey
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute

internal fun Route.publicKey(config: ApplicationConfig) {
    get(AuthRoute.OauthPublicKey.route) {
        call.respond(mapOf("jwt" to config.publicKey))
    }
}
