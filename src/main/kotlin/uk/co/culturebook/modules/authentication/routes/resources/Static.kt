package uk.co.culturebook.modules.authentication.routes.resources

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import uk.co.culturebook.Constants.WellKnownRoute
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute


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
    static(WellKnownRoute) {
        staticBasePackage = "assets"
        resource("assetlinks.json")
    }
}