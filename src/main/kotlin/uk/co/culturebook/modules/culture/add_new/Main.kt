package uk.co.culturebook.modules.culture.add_new

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.culture.add_new.routes.*

fun Application.addNewModule() {
    routing {
        authenticate(AuthRoute.JwtAuth.route) {
            route(uk.co.culturebook.modules.culture.data.data.interfaces.AddNewRoute.AddNewVersion.V1.route) {
                addNewCulture()
                getCulture()

                submitElement()
                getElementRoutes()

                getContributionRoutes()
                uploadContributionRoute()
            }
        }
    }
}