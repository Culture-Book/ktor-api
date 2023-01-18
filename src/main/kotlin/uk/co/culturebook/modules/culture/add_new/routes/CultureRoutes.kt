package uk.co.culturebook.modules.culture.add_new.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.culture.add_new.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.add_new.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.add_new.data.models.Location
import uk.co.culturebook.modules.culture.add_new.logic.addCulture
import uk.co.culturebook.modules.culture.add_new.logic.getCultureById
import uk.co.culturebook.modules.culture.add_new.logic.getCulturesByLocation
import uk.co.culturebook.utils.generateUUID

internal fun Route.addNewCulture() {
    post(AddNewRoute.Cultures.route) {
        when (val add = addCulture(call.receive())) {
            is CultureState.Error -> call.respond(HttpStatusCode.BadRequest, add)
            is CultureState.Success.AddCulture -> call.respond(add.culture)
            else -> {}
        }
    }
}

internal fun Route.getCulture() {
    get(AddNewRoute.Cultures.route) {
        val location = call.receive<Location>()
        call.respond(getCulturesByLocation(location))
    }

    get(AddNewRoute.Cultures.route) {
        val id = call.request.queryParameters[AddNewRoute.Culture.idParam].generateUUID()
        call.respond(getCultureById(id))
    }
}