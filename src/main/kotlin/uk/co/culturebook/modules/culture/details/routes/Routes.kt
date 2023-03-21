package uk.co.culturebook.modules.culture.details.routes

import uk.co.culturebook.modules.culture.data.database.repositories.CommentRepository
import uk.co.culturebook.modules.culture.data.database.repositories.ReactionRepository
import uk.co.culturebook.modules.culture.data.models.RequestComment
import uk.co.culturebook.modules.culture.data.models.RequestReaction
import java.util.*

suspend fun postComment(requestComment: RequestComment, userId: String) =
    if (requestComment.elementId != null) {
        CommentRepository.insertElementComment(requestComment.comment, requestComment.elementId, userId)
    } else if (requestComment.contributionId != null) {
        CommentRepository.insertContributionComment(requestComment.comment, requestComment.contributionId, userId)
    } else {
        null
    }

suspend fun deleteComment(commentId: UUID, isContribution: Boolean, userId: String) =
    if (!isContribution) {
        CommentRepository.deleteElementComment(commentId, userId)
    } else {
        CommentRepository.deleteContributionComment(commentId, userId)
    }

suspend fun postReaction(requestReaction: RequestReaction, userId: String) =
    if (requestReaction.elementId != null) {
        ReactionRepository.toggleElementReaction(requestReaction.elementId, requestReaction.reaction, userId)
    } else if (requestReaction.contributionId != null) {
        ReactionRepository.toggleContributionReaction(requestReaction.contributionId, requestReaction.reaction, userId)
    } else {
        null
    }

suspend fun blockComment(requestComment: RequestComment, userId: String) =
    if (requestComment.elementId != null) {
        CommentRepository.blockElementComment(requestComment.comment, userId)
    } else if (requestComment.contributionId != null) {
        CommentRepository.blockContributionComment(requestComment.comment, userId)
    } else {
        null
    }

suspend fun getComments(elementId: UUID? = null, contributionId: UUID? = null, userId: String) =
    if (elementId != null) {
        CommentRepository.getElementComments(elementId, userId)
    } else if (contributionId != null) {
        CommentRepository.getContributionComments(contributionId, userId)
    } else {
        emptyList()
    }