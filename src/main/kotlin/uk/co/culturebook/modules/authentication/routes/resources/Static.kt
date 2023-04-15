package uk.co.culturebook.modules.authentication.routes.resources

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.Constants.WellKnownRoute
import uk.co.culturebook.modules.authentication.data.AuthConfig.assetLink
import uk.co.culturebook.modules.authentication.data.AuthConfig.privacyLink
import uk.co.culturebook.modules.authentication.data.AuthConfig.toSLink
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute


internal fun Route.getTos() {
    static(AuthRoute.User.Tos.route) {
        get("EULA.html") {
            val tosLink = call.application.environment.config.toSLink
            call.respondRedirect(tosLink, true)
        }
    }
}

internal fun Route.getPrivacy() {
    static(AuthRoute.User.Privacy.route) {
        get("Privacy_Notice.html") {
            val privacyLink = call.application.environment.config.privacyLink
            call.respondRedirect(privacyLink, true)
        }
    }
}

internal fun Route.getAssetLinks() {
    static(WellKnownRoute) {
        get("assetlinks.json") {
            val assetLink = call.application.environment.config.assetLink
            call.respondRedirect(assetLink, true)
        }
    }
}