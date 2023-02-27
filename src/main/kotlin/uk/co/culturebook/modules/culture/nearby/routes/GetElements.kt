package uk.co.culturebook.modules.culture.nearby.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.culture.data.data.interfaces.ElementRoute
import uk.co.culturebook.modules.culture.data.database.repositories.MediaRepository
import uk.co.culturebook.modules.culture.data.models.SearchCriteria
import uk.co.culturebook.modules.culture.nearby.logic.getContributions
import uk.co.culturebook.modules.culture.nearby.logic.getElements
import uk.co.culturebook.modules.culture.nearby.logic.getNearbyElements
import uk.co.culturebook.utils.forceNotNull
import uk.co.culturebook.utils.toUUID

internal fun Route.getElementsRoute() {
    post(ElementRoute.Elements.route) {
        val criteria = call.receive<SearchCriteria>()
        val elements = with(criteria) {
            if (location != null) {
                getNearbyElements(location, types, radius, page)
            } else if (!criteria.searchString.isNullOrEmpty()) {
                getElements(criteria.searchString, types, radius, page)
            } else {
                emptyList()
            }
        }
        call.respond(HttpStatusCode.OK, elements)
    }
    get(ElementRoute.ElementsMedia.route) {
        val elementId = call.request.queryParameters[ElementRoute.ElementsMedia.ElementId].forceNotNull(call).toUUID()
        val media = MediaRepository.getMediaByElement(elementId)
        call.respond(HttpStatusCode.OK, media)
    }
}

internal fun Route.getContributionRoute() {
    post(ElementRoute.Contributions.route) {
        val criteria = call.receive<SearchCriteria>()
        val elementId = criteria.elementId.forceNotNull(call)
        val searchString = criteria.searchString.forceNotNull(call)
        val contributions = getContributions(elementId, searchString, criteria.types, criteria.page)
        call.respond(HttpStatusCode.OK, contributions)
    }
    get(ElementRoute.ContributionsMedia.route) {
        val contributionId =
            call.request.queryParameters[ElementRoute.ContributionsMedia.ContributionId].forceNotNull(call).toUUID()
        val media = MediaRepository.getMediaByContribution(contributionId)
        call.respond(HttpStatusCode.OK, media)
    }
}