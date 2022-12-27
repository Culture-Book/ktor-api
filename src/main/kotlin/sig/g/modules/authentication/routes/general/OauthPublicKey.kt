package sig.g.modules.authentication.routes.general

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.modules.authentication.constants.AuthRoute

internal fun Route.oauthPublicKey() {
    get(AuthRoute.OauthPublicKey.route) {
        call.respond(AppConfig.OAuthConfig.PublicKey.getProperty())
    }
}
