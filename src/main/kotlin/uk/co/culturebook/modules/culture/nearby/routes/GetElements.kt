package uk.co.culturebook.modules.culture.nearby.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import uk.co.culturebook.utils.forceNotNull

internal fun Route.getElements() {
    post("elements") {
        val location = call.request.queryParameters["location"].forceNotNull(call)
        val types = call.request.queryParameters["location"].forceNotNull(call)
    }
}