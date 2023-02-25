package uk.co.culturebook.modules.culture.nearby

import io.ktor.server.application.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.culture.add_new.routes.*
import uk.co.culturebook.modules.culture.data.data.interfaces.AddNewRoute

fun Application.addNewModule() {
    routing {
        route(AddNewRoute.AddNewVersion.V1.route) {
            addNewCulture()
            getCulture()

            submitElement()
            getElementRoutes()

            getContributionRoutes()
            uploadContributionRoute()
        }
    }
}