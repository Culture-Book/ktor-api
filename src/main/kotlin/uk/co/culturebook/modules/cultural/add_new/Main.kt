package uk.co.culturebook.modules.cultural.add_new

import io.ktor.server.application.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.cultural.add_new.interfaces.AddNewRoute
import uk.co.culturebook.modules.cultural.add_new.location.routes.addNewCulture
import uk.co.culturebook.modules.cultural.add_new.location.routes.getCulturesByLocationRoute

fun Application.addNewModule() {
    routing {
        route(AddNewRoute.AddNewVersion.V1.route) {
            addNewCulture()
            getCulturesByLocationRoute()
        }
    }
}