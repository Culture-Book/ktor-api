package sig.g.authentication

import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.junit.Test
import sig.g.BaseApplicationTest
import sig.g.modules.authentication.data.models.User
import sig.g.modules.authentication.data.models.states.AuthError
import sig.g.modules.authentication.decodeJwt
import sig.g.modules.authentication.encodeOAuth
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class AuthenticationTest : BaseApplicationTest() {

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

        val email = "email1@email.com".encodeOAuth()
        val password = "password123".encodeOAuth()
        val user = User(email = email!!, password = password!!)

        it.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.Created)
            assertNotNull(setCookie()["UserSession"])
            print(setCookie()["UserSession"]?.value)
        }

    }

    @Test
    fun `test login user, logins user`() = testApp {

        val email = "email1@email.com".encodeOAuth()
        val password = "password123".encodeOAuth()
        val user = User(email = email!!, password = password!!)

        it.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.Created)
            assertNotNull(setCookie()["UserSession"])
            print(setCookie()["UserSession"]?.value)
        }

        it.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.Created)
            assertNotNull(setCookie()["UserSession"])
            print(setCookie()["UserSession"]?.value)
        }

    }

    @Test
    fun `test register duplicate user, returns 400`() = testApp {

        val email = "email@email.com".encodeOAuth()
        val password = "password123".encodeOAuth()
        val user = User(email = email!!, password = password!!)

        it.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.Created)
        }

        it.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.BadRequest)
            assertEquals(call.body(), AuthError.DuplicateEmail)
        }
    }

    @Test
    fun `test register user, unencrypted password and email`() = testApp {

        val email = "email@email.com"
        val password = "password123"
        val user = User(email = email, password = password)

        it.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.apply {
            assertEquals(status, HttpStatusCode.BadRequest)
            assertNotEquals(call.body(), user.userId)
        }
    }

}