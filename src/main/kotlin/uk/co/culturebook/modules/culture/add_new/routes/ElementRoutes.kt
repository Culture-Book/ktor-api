package uk.co.culturebook.modules.culture.add_new.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.culture.add_new.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.add_new.data.models.Element
import uk.co.culturebook.modules.culture.add_new.data.models.isValidElementTypeName
import uk.co.culturebook.modules.culture.add_new.logic.addElement
import uk.co.culturebook.modules.culture.add_new.logic.getDuplicateElements
import uk.co.culturebook.utils.forceNotNull

internal fun Route.getElementRoutes() {
    get(AddNewRoute.Element.Duplicate.route) {
        val name = call.request.queryParameters[AddNewRoute.Element.Duplicate.nameParam].forceNotNull(call)
        val type = call.request.queryParameters[AddNewRoute.Element.Duplicate.typeParam].forceNotNull(call)

        if (!type.isValidElementTypeName()) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val duplicates = getDuplicateElements(name, type)

        if (duplicates.isEmpty()) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.Conflict, duplicates)
    }
}

internal fun Route.addElementRoutes() {
    post(AddNewRoute.Element.route) {
        val callElement = call.receive<Element>()
        val element = addElement(callElement)

        if (element != null) {
            call.respond(HttpStatusCode.OK, element)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}