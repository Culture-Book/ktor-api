package uk.co.culturebook.modules.culture.add_new

import io.ktor.server.application.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.culture.add_new.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.add_new.routes.addElementRoutes
import uk.co.culturebook.modules.culture.add_new.routes.addNewCulture
import uk.co.culturebook.modules.culture.add_new.routes.getCulture
import uk.co.culturebook.modules.culture.add_new.routes.getElementRoutes

fun Application.addNewModule() {
    routing {
        route(AddNewRoute.AddNewVersion.V1.route) {
            addNewCulture()
            getCulture()
            addElementRoutes()
            getElementRoutes()
        }
    }
}