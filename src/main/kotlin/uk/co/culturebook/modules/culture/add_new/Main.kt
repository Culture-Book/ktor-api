package uk.co.culturebook.modules.culture.add_new

import io.ktor.server.application.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.culture.add_new.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.add_new.location.routes.addNewCulture
import uk.co.culturebook.modules.culture.add_new.location.routes.getCulture

fun Application.addNewModule() {
    routing {
        route(AddNewRoute.AddNewVersion.V1.route) {
            addNewCulture()
            getCulture()
        }
    }
}