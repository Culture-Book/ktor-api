package uk.co.culturebook.modules.culture.nearby.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserId
import uk.co.culturebook.modules.culture.data.data.interfaces.NearbyRoute
import uk.co.culturebook.modules.culture.data.database.repositories.FavouriteRepository
import uk.co.culturebook.modules.culture.data.models.FavouriteElement
import uk.co.culturebook.utils.forceNotNull

internal fun Route.favRoutes() {
    post(NearbyRoute.FavouriteElement.route) {
        val favouriteElement = call.receive<FavouriteElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        val favExists = FavouriteRepository.favElementExists(getUserId(), favouriteElement)
        if (favExists) {
            FavouriteRepository.unfavouriteElement(getUserId(), favouriteElement)
        } else {
            FavouriteRepository.favouriteElement(getUserId(), favouriteElement)
        }

        call.respond(HttpStatusCode.OK)
    }
    post(NearbyRoute.FavouriteContribution.route) {
        val favouriteElement = call.receive<FavouriteElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        val favExists = FavouriteRepository.favContributionExists(getUserId(), favouriteElement)
        if (favExists) {
            FavouriteRepository.unfavouriteContribution(getUserId(), favouriteElement)
        } else {
            FavouriteRepository.favouriteContribution(getUserId(), favouriteElement)
        }
        call.respond(HttpStatusCode.OK)
    }
    post(NearbyRoute.FavouriteCulture.route) {
        val favouriteElement = call.receive<FavouriteElement>()
            .forceNotNull(call)
            .uuid
            .forceNotNull(call)

        val favExists = FavouriteRepository.favCultureExists(getUserId(), favouriteElement)
        if (favExists) {
            FavouriteRepository.unfavouriteCulture(getUserId(), favouriteElement)
        } else {
            FavouriteRepository.favouriteCulture(getUserId(), favouriteElement)
        }
        call.respond(HttpStatusCode.OK)
    }
}