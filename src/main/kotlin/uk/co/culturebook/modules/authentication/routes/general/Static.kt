package uk.co.culturebook.modules.authentication.routes.general

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import uk.co.culturebook.config.WellKnown
import uk.co.culturebook.modules.authentication.constants.AuthRoute


internal fun Route.getTos() {
    static(AuthRoute.User.Tos.route) {
        staticBasePackage = "docs"
        resource("EULA.html")
    }
}

internal fun Route.getPrivacy() {
    static(AuthRoute.User.Privacy.route) {
        staticBasePackage = "docs"
        resource("Privacy_Notice.html")
    }
}

internal fun Route.getAssetLinks() {
    static(WellKnown.route) {
        staticBasePackage = "assets"
        resource("assetlinks.json")
    }
}