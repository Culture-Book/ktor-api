package sig.g.modules.authentication.routes.general

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import sig.g.modules.authentication.constants.AuthRoute


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