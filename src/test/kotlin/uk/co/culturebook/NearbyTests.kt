package uk.co.culturebook

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.junit.Test
import uk.co.culturebook.base.BaseTest
import uk.co.culturebook.base.MockData
import uk.co.culturebook.modules.culture.data.models.Element
import kotlin.test.assertEquals

class NearbyTests : BaseTest() {

    @Test
    fun insertElements() {
        testAuthenticated {
            it.post("/add_new/v1/culture") {
                setBody(MockData.cultureRequest1)
                contentType(ContentType.Application.Json)
            }
            val response = it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            assertEquals(response.status, HttpStatusCode.OK)
        }
    }

    // Add 2 cultures and 2 elements, then search for nearby elements, it should return the closest element, not all of them
    @Test
    fun getNearbyElements() {
        testAuthenticated {
            val cultureRes = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest1)
            }
            assertEquals(cultureRes.status, HttpStatusCode.OK)

            val cultureRes2 = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest2)
            }
            assertEquals(cultureRes2.status, HttpStatusCode.OK)

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element2))
                    }
                ))
            }
            val response = it.post("elements/v1/elements") {
                contentType(ContentType.Application.Json)
                setBody(MockData.searchCriteria1)
            }
            val listOfElements = response.body<List<Element>>()
            assertEquals(listOfElements, listOf(MockData.element1))
        }
    }

    @Test
    fun testFilters() {
        testAuthenticated {
            val cultureRes = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest1)
            }
            assertEquals(cultureRes.status, HttpStatusCode.OK)

            val cultureRes2 = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest2)
            }
            assertEquals(cultureRes2.status, HttpStatusCode.OK)

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.differentNameElement1))
                    }
                ))
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element2))
                    }
                ))
            }


            val response = it.post("elements/v1/elements") {
                contentType(ContentType.Application.Json)
                setBody(MockData.noTypesCriteria)
            }

            val listOfElements = response.body<List<Element>>()
            assertEquals(listOfElements, listOf())
        }
    }

    @Test
    fun testSearchString() {
        testAuthenticated {
            val cultureRes = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest1)
            }
            assertEquals(cultureRes.status, HttpStatusCode.OK)

            val cultureRes2 = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest2)
            }
            assertEquals(cultureRes2.status, HttpStatusCode.OK)

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element2))
                    }
                ))
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.differentNameElement1))
                    }
                ))
            }

            val response = it.post("elements/v1/elements") {
                contentType(ContentType.Application.Json)
                setBody(MockData.getElement1Criteria)
            }

            val listOfElements = response.body<List<Element>>()
            assertEquals(listOfElements, listOf(MockData.element1))
        }
    }

    @Test
    fun getElementDetails() {
        testAuthenticated {
            val cultureRes = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest1)
            }
            assertEquals(cultureRes.status, HttpStatusCode.OK)

            val cultureRes2 = it.post("/add_new/v1/culture") {
                contentType(ContentType.Application.Json)
                setBody(MockData.cultureRequest2)
            }
            assertEquals(cultureRes2.status, HttpStatusCode.OK)

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element1))
                    }
                ))
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.element2))
                    }
                ))
            }

            it.submitForm("/add_new/v1/element/submit") {
                contentType(ContentType.MultiPart.Any)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("element", Json.encodeToString(Element.serializer(), MockData.differentNameElement1))
                    }
                ))
            }

            val response = it.get("elements/v1/element?element_id=${MockData.element1.id}")
            val element = response.body<Element>()
            assertEquals(element, MockData.element1)
        }
    }

    @Test
    fun testBlockedElement() {
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

            val blockResponse = it.post("elements/v1/block/element") {
                contentType(ContentType.Application.Json)
                setBody(MockData.blockedElement)
            }
            assertEquals(blockResponse.status, HttpStatusCode.OK)

            val response = it.post("elements/v1/elements") {
                contentType(ContentType.Application.Json)
                setBody(MockData.getElement1Criteria)
            }

            val listOfElements = response.body<List<Element>>()
            assertEquals(listOfElements, listOf())
        }
    }
}