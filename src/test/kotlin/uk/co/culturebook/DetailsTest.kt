package uk.co.culturebook

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.junit.Test
import uk.co.culturebook.base.BaseTest
import uk.co.culturebook.base.MockData
import uk.co.culturebook.modules.culture.data.models.Comment
import uk.co.culturebook.modules.culture.data.models.Element
import uk.co.culturebook.modules.culture.data.models.RequestComment
import kotlin.test.assertEquals

class DetailsTest : BaseTest() {
    @Test
    fun testAddAReaction() {
        testAuthenticated {
            it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest1)
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            val reactionResponse = it.post("v1/elements/reactions") {
                contentType(ContentType.Application.Json)
                setBody(MockData.reactionRequest)
            }
            assertEquals(reactionResponse.status, HttpStatusCode.OK)

            val elementResponse = it.get("elements/v1/element?element_id=${MockData.element1.id}")
            val elementWithReaction = elementResponse.body<Element>()
            assertEquals(elementWithReaction.reactions.first().reaction, MockData.reactionRequest.reaction.reaction)
        }
    }

    @Test
    fun testAddAComment() {
        testAuthenticated {
            it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest1)
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            val reactionResponse = it.post("v1/elements/comments") {
                contentType(ContentType.Application.Json)
                setBody(MockData.commentRequest)
            }
            assertEquals(reactionResponse.status, HttpStatusCode.OK)

            val comments =
                it.get("v1/elements/comments?elementId=${MockData.element1.id}").body<List<Comment>>()
            assertEquals(comments.first().comment, MockData.commentRequest.comment.comment)
        }
    }

    @Test
    fun testBlockedComment() {
        testAuthenticated {
            it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest1)
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            val commentResponse = it.post("v1/elements/comments") {
                contentType(ContentType.Application.Json)
                setBody(MockData.commentRequest)
            }
            assertEquals(commentResponse.status, HttpStatusCode.OK)

            val comments = it.get("v1/elements/comments?elementId=${MockData.element1.id}").body<List<Comment>>()

            val blockCommentRequest = RequestComment(
                elementId = MockData.element1.id,
                comment = comments.first(),
            )

            val blockResponse = it.post("v1/elements/comments/block") {
                contentType(ContentType.Application.Json)
                setBody(blockCommentRequest)
            }
            assertEquals(blockResponse.status, HttpStatusCode.OK)

            val blockedComments =
                it.get("v1/elements/comments?elementId=${MockData.element1.id}").body<List<Comment>>()
            assertEquals(blockedComments, listOf())
        }
    }
}