package uk.co.culturebook.modules.culture.details

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserId
import uk.co.culturebook.modules.culture.data.data.interfaces.DetailsRoute
import uk.co.culturebook.modules.culture.data.models.RequestComment
import uk.co.culturebook.modules.culture.data.models.RequestReaction
import uk.co.culturebook.modules.culture.details.routes.*
import uk.co.culturebook.utils.forceNotNull
import uk.co.culturebook.utils.toUUID

fun Application.detailsModule() {
    routing {
        authenticate(AuthRoute.JwtAuth.route) {
            route(DetailsRoute.Version.V1.route) {
                route(DetailsRoute.Elements.route) {
                    elementRoutes()
                }

                route(DetailsRoute.Contributions.route) {
                    contributionRoutes()
                }
            }
        }
    }
}

private fun Route.elementRoutes() {
    get(DetailsRoute.Comments.route) {
        val elementId = call.request.queryParameters[DetailsRoute.Elements.elementId].forceNotNull(call).toUUID()
        val comments = getComments(elementId = elementId, userId = getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, comments)
    }

    post(DetailsRoute.Comments.route) {
        val requestComment = call.receive<RequestComment>()
        val comment = postComment(requestComment, getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, comment)
    }

    delete(DetailsRoute.Comments.route) {
        val commentId = call.request.queryParameters[DetailsRoute.Comments.commentId].forceNotNull(call).toUUID()
        val isContribution =
            call.request.queryParameters[DetailsRoute.Comments.isContribution].forceNotNull(call).toBoolean()
        val deleted = deleteComment(commentId, isContribution, getUserId()).forceNotNull(call)
        if (deleted) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }

    post(DetailsRoute.Comments.BlockComment.route) {
        val requestComment = call.receive<RequestComment>()
        val comment = blockComment(requestComment, getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, comment)
    }

    post(DetailsRoute.Reactions.route) {
        val requestReaction = call.receive<RequestReaction>()
        val reaction = postReaction(requestReaction, getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, reaction)
    }
}

private fun Route.contributionRoutes() {
    get(DetailsRoute.Comments.route) {
        val contributionId =
            call.request.queryParameters[DetailsRoute.Contributions.contributionId].forceNotNull(call).toUUID()
        val comments = getComments(contributionId = contributionId, userId = getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, comments)
    }

    post(DetailsRoute.Comments.route) {
        val requestComment = call.receive<RequestComment>()
        val comment = postComment(requestComment, getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, comment)
    }

    post(DetailsRoute.Comments.BlockComment.route) {
        val requestComment = call.receive<RequestComment>()
        val comment = blockComment(requestComment, getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, comment)
    }

    delete(DetailsRoute.Comments.route) {
        val commentId = call.request.queryParameters[DetailsRoute.Comments.commentId].forceNotNull(call).toUUID()
        val isContribution =
            call.request.queryParameters[DetailsRoute.Comments.isContribution].forceNotNull(call).toBoolean()
        val deleted = deleteComment(commentId, isContribution, getUserId()).forceNotNull(call)
        if (deleted) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
    }

    post(DetailsRoute.Reactions.route) {
        val requestReaction = call.receive<RequestReaction>()
        val reaction = postReaction(requestReaction, getUserId()).forceNotNull(call)
        call.respond(HttpStatusCode.OK, reaction)
    }
}