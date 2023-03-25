package uk.co.culturebook.modules.culture.elements

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.culture.data.data.interfaces.ElementsRoute
import uk.co.culturebook.modules.culture.elements.routes.*

fun Application.nearbyModule() {
    routing {
        authenticate(AuthRoute.JwtAuth.route) {
            route(ElementsRoute.Version.V1.route) {
                getElementsRoute()
                getContributionRoute()
                getCulturesRoute()
                blockRoutes()
                favRoutes()
            }
        }
    }
}