package uk.co.culturebook.modules.culture.nearby

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.culture.data.data.interfaces.NearbyRoute
import uk.co.culturebook.modules.culture.nearby.routes.blockRoutes
import uk.co.culturebook.modules.culture.nearby.routes.getContributionRoute
import uk.co.culturebook.modules.culture.nearby.routes.getCulturesRoute
import uk.co.culturebook.modules.culture.nearby.routes.getElementsRoute

fun Application.nearbyModule() {
    routing {
        authenticate(AuthRoute.JwtAuth.route) {
            route(NearbyRoute.Version.V1.route) {
                getElementsRoute()
                getContributionRoute()
                getCulturesRoute()
                blockRoutes()
            }
        }
    }
}