package uk.co.culturebook.modules.authentication.routes.general

import uk.co.culturebook.modules.authentication.constants.AuthRoute
import io.ktor.server.http.content.*
import io.ktor.server.routing.*


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