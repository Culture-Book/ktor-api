package uk.co.culturebook.modules.culture.nearby.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserId
import uk.co.culturebook.modules.culture.data.data.interfaces.NearbyRoute
import uk.co.culturebook.modules.culture.data.database.repositories.MediaRepository
import uk.co.culturebook.modules.culture.data.models.SearchCriteria
import uk.co.culturebook.modules.culture.nearby.logic.getContributions
import uk.co.culturebook.modules.culture.nearby.logic.getCultures
import uk.co.culturebook.modules.culture.nearby.logic.getElements
import uk.co.culturebook.modules.culture.nearby.logic.getNearbyElements
import uk.co.culturebook.utils.forceNotNull
import uk.co.culturebook.utils.toUUID

internal fun Route.getElementsRoute() {
    post(NearbyRoute.Elements.route) {
        val criteria = call.receive<SearchCriteria>()
        val elements = with(criteria) {
            if (!criteria.searchString.isNullOrEmpty()) {
                getElements(getUserId(), criteria.searchString, types, radius, page)
            } else if (location != null) {
                getNearbyElements(getUserId(), location, types, radius, page)
            } else {
                emptyList()
            }
        }
        call.respond(HttpStatusCode.OK, elements)
    }
    get(NearbyRoute.ElementsMedia.route) {
        val elementId = call.request.queryParameters[NearbyRoute.ElementsMedia.ElementId].forceNotNull(call).toUUID()
        val media = MediaRepository.getMediaByElement(elementId)
        call.respond(HttpStatusCode.OK, media)
    }
}

internal fun Route.getContributionRoute() {
    post(NearbyRoute.Contributions.route) {
        val criteria = call.receive<SearchCriteria>()
        val elementId = criteria.elementId
        val searchString = criteria.searchString ?: ""
        val contributions =
            if (elementId != null) {
                getContributions(getUserId(), elementId, searchString, criteria.types, criteria.page)
            } else {
                getContributions(getUserId(), searchString, criteria.types, criteria.page)
            }
        call.respond(HttpStatusCode.OK, contributions)
    }
    get(NearbyRoute.ContributionsMedia.route) {
        val contributionId =
            call.request.queryParameters[NearbyRoute.ContributionsMedia.ContributionId].forceNotNull(call).toUUID()
        val media = MediaRepository.getMediaByContribution(contributionId)
        call.respond(HttpStatusCode.OK, media)
    }
}

internal fun Route.getCulturesRoute() {
    post(NearbyRoute.Cultures.route) {
        val criteria = call.receive<SearchCriteria>()
        val searchString = criteria.searchString.forceNotNull(call)
        val contributions = getCultures(getUserId(), searchString)
        call.respond(HttpStatusCode.OK, contributions)
    }
}