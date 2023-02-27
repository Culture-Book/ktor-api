package uk.co.culturebook.modules.culture.nearby

import io.ktor.server.application.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.culture.data.data.interfaces.ElementRoute
import uk.co.culturebook.modules.culture.nearby.routes.getContributionRoute
import uk.co.culturebook.modules.culture.nearby.routes.getElementsRoute

fun Application.nearbyModule() {
    routing {
        route(ElementRoute.Version.V1.route) {
            getElementsRoute()
            getContributionRoute()
        }
    }
}