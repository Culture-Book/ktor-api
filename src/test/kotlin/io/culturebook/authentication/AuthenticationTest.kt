package io.culturebook.authentication

import io.culturebook.BaseApplicationTest
import io.culturebook.modules.authentication.data.models.User
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.decodeJwt
import io.culturebook.modules.authentication.encodeOAuth
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class AuthenticationTest : BaseApplicationTest() {
    private val email = "email1@email.com".encodeOAuth()
    private val password = "password123".encodeOAuth()
    private val user = User(email = email!!, password = password!!)

    @Test
    fun `test encoding works`() {
        val original = "Hello World!"
        val encrypted = original.encodeOAuth()
        val encryptedOauth = original.encodeOAuth()

        assertNotEquals(encrypted, original)
        assertNotEquals(encryptedOauth, original)
        assertNotNull(encrypted)
        assertNotNull(encryptedOauth)

        // Test empty string
        val original1 = ""
        val encrypted1 = original1.encodeOAuth()
        val encryptedOauth1 = original.encodeOAuth()


        assertNotEquals(encrypted1, original1)
        assertNotEquals(encryptedOauth1, original1)
        assertNotNull(encrypted1)
        assertNotNull(encryptedOauth1)
    }

    @Test
    fun `test decoding works`() {
        val original = "Hello World!"
        val encrypted = original.encodeOAuth()

        assertNotEquals(encrypted, original)
        assertNotNull(encrypted)

        val decoded = encrypted.decodeJwt()
        assertEquals(original, decoded)

        val encrypted1 = original.encodeOAuth()

        assertNotEquals(encrypted1, original)
        assertNotNull(encrypted1)

        val decoded1 = encrypted1.decodeJwt()
        assertEquals(original, decoded1)
    }

    @Test
    fun `test register user, registers user`() = testApp {

        it.post("/auth/v1/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.Created)
            assertNotNull(setCookie()["UserSession"])
        }

    }

    @Test
    fun `test login user, logins user`() = testApp {

        it.registerUser()

        it.post("/auth/v1/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.OK)
            assertNotNull(setCookie()["UserSession"])
        }

    }

    @Test
    fun `test register duplicate user, returns 400`() = testApp {

        it.post("/auth/v1/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.Created)
        }

        it.post("/auth/v1/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.BadRequest)
            assertEquals(call.body(), AuthState.Error.DuplicateEmail)
        }
    }

    @Test
    fun `test register user, unencrypted password and email`() = testApp {

        val email = "email@email.com"
        val password = "password123"
        val user = User(email = email, password = password)

        it.post("/auth/v1/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.BadRequest)
            assertNotEquals(call.body(), user.userId)
        }
    }

    private suspend fun HttpClient.registerUser() = post("/auth/v1/register") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }.apply {
        assertEquals(status, HttpStatusCode.Created)
        assertNotNull(setCookie()["UserSession"])
    }
}