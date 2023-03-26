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
import uk.co.culturebook.utils.toUUID

internal fun Route.blockRoutes() {

    get(ElementsRoute.BlockedList.route) {
        val blockedElements = BlockedElementsRepository.getBlockedLists(getUserId())
        call.respond(HttpStatusCode.OK, blockedElements)
    }

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
        val blockedElement = call.parameters[ElementsRoute.Element.Id]
            .forceNotNull(call)
            .toUUID()

        BlockedElementsRepository.unblockElement(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
    delete(ElementsRoute.BlockContribution.route) {
        val blockedElement = call.parameters[ElementsRoute.Contribution.Id]
            .forceNotNull(call)
            .toUUID()

        BlockedElementsRepository.unblockContribution(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
    delete(ElementsRoute.BlockCulture.route) {
        val blockedElement = call.parameters[ElementsRoute.Culture.Id]
            .forceNotNull(call)
            .toUUID()

        BlockedElementsRepository.unblockCulture(getUserId(), blockedElement)
        call.respond(HttpStatusCode.OK)
    }
}