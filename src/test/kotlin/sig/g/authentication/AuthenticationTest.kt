package sig.g.authentication

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import org.junit.Test
import sig.g.BaseApplicationTest
import sig.g.modules.authentication.configureSecurity
import sig.g.modules.authentication.decrypt
import sig.g.modules.authentication.encrypt
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class AuthenticationTest : BaseApplicationTest() {

    @Test
    fun `test public key endpoint`() = testApplication {
        application {
            configureSecurity()
        }
        client.get(".well-known/public").apply {
            assertEquals(
                bodyAsText(),
                "MFswDQYJKoZIhvcNAQEBBQADSgAwRwJAa9AWWKdnZeUyTUZFMiQ3J1l7ZkdXUDlFM3OAbxDqqAsE5Xu9en20KNemdmGMaN1NL2WeZN1iTqQAuLRyxQ5zSQIDAQAB"
            )
        }
    }

    @Test
    fun `test encoding works`() {
        val original = "Hello World!"
        val encrypted = original.encrypt()

        assertNotEquals(encrypted, original)
        assertNotNull(encrypted)

        // Test empty string
        val original1 = ""
        val encrypted1 = original1.encrypt()

        assertNotEquals(encrypted1, original1)
        assertNotNull(encrypted1)
    }

    @Test
    fun `test decoding works`() {
        val original = "Hello World!"
        val encrypted = original.encrypt()

        assertNotEquals(encrypted, original)
        assertNotNull(encrypted)

        val decoded = encrypted.decrypt()
        assertEquals(original, decoded)
    }
}