package uk.co.culturebook.modules.culture.add_new.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserId
import uk.co.culturebook.modules.culture.add_new.logic.addCulture
import uk.co.culturebook.modules.culture.add_new.logic.getCultureById
import uk.co.culturebook.modules.culture.add_new.logic.getCulturesByLocation
import uk.co.culturebook.modules.culture.data.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.data.models.CultureRequest
import uk.co.culturebook.modules.culture.data.models.Location
import uk.co.culturebook.utils.toUUID

internal fun Route.addNewCulture() {
    post(AddNewRoute.Culture.route) {
        val addCultureRequest = call.receive<CultureRequest>()
        when (val add = addCulture(getUserId(), addCultureRequest.culture, addCultureRequest.location)) {
            is CultureState.Error -> call.respond(HttpStatusCode.BadRequest, add)
            is CultureState.Success.AddCulture -> call.respond(add.culture)
            else -> {}
        }
    }
}

internal fun Route.getCulture() {
    post(AddNewRoute.Cultures.route) {
        val location = call.receive<Location>()
        val userId = getUserId()
        call.respond(getCulturesByLocation(userId, location))
    }

    get(AddNewRoute.Culture.route) {
        val id = call.request.queryParameters[AddNewRoute.Culture.idParam].toUUID()
        call.respond(getCultureById(id))
    }
}