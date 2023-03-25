package uk.co.culturebook.modules.culture.elements.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserId
import uk.co.culturebook.modules.culture.data.data.interfaces.ElementsRoute
import uk.co.culturebook.modules.culture.data.database.repositories.BlockedElementsRepository
import uk.co.culturebook.modules.culture.data.models.BlockedElement
import uk.co.culturebook.utils.forceNotNull

internal fun Route.blockRoutes() {
    post(ElementsRoute.BlockElement.route) {
        val blockedElement = call.receive<BlockedElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        BlockedElementsRepository.blockElement(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
    post(ElementsRoute.BlockContribution.route) {
        val blockedElement = call.receive<BlockedElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        BlockedElementsRepository.blockContribution(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
    post(ElementsRoute.BlockCulture.route) {
        val blockedElement = call.receive<BlockedElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        BlockedElementsRepository.blockCulture(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }

    delete(ElementsRoute.BlockElement.route) {
        val blockedElement = call.receive<BlockedElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        BlockedElementsRepository.unblockElement(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
    delete(ElementsRoute.BlockContribution.route) {
        val blockedElement = call.receive<BlockedElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        BlockedElementsRepository.unblockContribution(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
    delete(ElementsRoute.BlockCulture.route) {
        val blockedElement = call.receive<BlockedElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        BlockedElementsRepository.unblockCulture(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
}