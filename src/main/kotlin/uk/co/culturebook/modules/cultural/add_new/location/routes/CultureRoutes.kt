package uk.co.culturebook.modules.cultural.add_new.location.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.cultural.add_new.interfaces.AddNewRoute
import uk.co.culturebook.modules.cultural.add_new.location.data.interfaces.LocationState
import uk.co.culturebook.modules.cultural.add_new.location.data.models.Location
import uk.co.culturebook.modules.cultural.add_new.location.logic.addCulture
import uk.co.culturebook.modules.cultural.add_new.location.logic.getCulturesByLocation

internal fun Route.addNewCulture() {
    post(AddNewRoute.Cultures.route) {
        when(val add = addCulture(call.receive())) {
            LocationState.Error.Generic -> call.respond(HttpStatusCode.BadRequest, add)
            LocationState.Error.DuplicateCulture -> call.respond(HttpStatusCode.BadRequest, add)
            is LocationState.Success.AddCulture -> call.respond(add.culture)
            is LocationState.Success.GetCultures -> {}
        }
    }
}

internal fun Route.getCulturesByLocationRoute() {
    get(AddNewRoute.Cultures.route) {
        val location = call.receive<Location>()
        call.respond(getCulturesByLocation(location))
    }
}