package uk.co.culturebook.auth

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.test.dispatcher.*
import junit.framework.TestCase.assertEquals
import org.junit.Test
import uk.co.culturebook.base.BaseTest
import uk.co.culturebook.modules.authentication.data.AuthConfig.publicKey
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.logic.general.login
import uk.co.culturebook.modules.authentication.logic.general.registerUser
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE
import kotlin.test.assertNotNull

class AuthTests : BaseTest() {
    private fun generateJavaPublicKey(publicKey: String): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeyDecoded = Base64.getDecoder().decode(publicKey)
        val publicEncodedKeySpec = X509EncodedKeySpec(publicKeyDecoded)
        return keyFactory.generatePublic(publicEncodedKeySpec)
    }

    private fun String.encrypt(key: String): String {
        val encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING").apply {
            init(ENCRYPT_MODE, generateJavaPublicKey(key))
        }
        val stringBytes = toByteArray(Charsets.ISO_8859_1)
        val secretBytes = encryptCipher.doFinal(stringBytes)

        return String(Base64.getEncoder().encode(secretBytes), StandardCharsets.ISO_8859_1)
    }

    @Test
    fun `when decoding a nice string, it should decode correctly`() {
        val niceString = "Hello, World!"
        val encodedString = niceString.encrypt(testConfig.publicKey)
        val decodedString = encodedString.decodeOAuth(testConfig)
        assertEquals(niceString, decodedString)
    }

    @Test
    fun `when decoding a bad string, it should return null`() {
        val badString = "Hello, World!"
        val decodedString = badString.decodeOAuth(testConfig)
        assertEquals(null, decodedString)
    }

    @Test
    fun `when a user registers successfully, generate a jwt`() {
        testSuspend {
            val email = "email@email.com".encrypt(testConfig.publicKey)
            val password = "password123".encrypt(testConfig.publicKey)

            val user = User(email = email, password = password)
            val registrationState = registerUser(testConfig, user)
            val jwt = (registrationState as? AuthState.Success)?.jwt
            assertNotNull(jwt)
        }
    }

    @Test
    fun `when a user logs in successfully, generate a jwt`() {
        testSuspend {

            // Register User
            val email = "email@email.com".encrypt(testConfig.publicKey)
            val password = "password123".encrypt(testConfig.publicKey)

            val user = User(email = email, password = password)
            val registrationState = registerUser(testConfig, user)
            val jwt = (registrationState as? AuthState.Success)?.jwt
            assertNotNull(jwt)

            // Login User
            val loginState = login(testConfig, user)
            val loginJwt = (loginState as? AuthState.Success)?.jwt
            assertNotNull(loginJwt)
        }
    }

    @Test
    fun `when registering user, should be able to authenticate`() {
        testSuspend {
            // Register User
            val email = "email@email.com".encrypt(testConfig.publicKey)
            val password = "password123".encrypt(testConfig.publicKey)

            val user = User(
                email = email,
                password = password,
                tosAccept = LocalDateTime.MIN,
                privacyAccept = LocalDateTime.MIN
            )

            test {
                val regResponse = it.post(
                    "http://localhost:80/" +
                            "${AuthRoute.AuthRouteVersion.V1.route}/" +
                            AuthRoute.Register.route
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(user)
                }
                val tokens = regResponse.body<AuthState.Success>()
                val userResponse =
                    it.get(
                        "http://localhost:80/" +
                                AuthRoute.AuthRouteVersion.V1.route + "/" +
                                AuthRoute.User.route
                    ) {
                        headers {
                            set("Authorization", "Bearer ${tokens.jwt}")
                        }
                    }
                assertEquals(userResponse.status, HttpStatusCode.OK)
            }
        }
    }

    @Test
    fun `when login user, should be able to authenticate`() {
        testSuspend {
            // Register User
            val email = "email@email.com".encrypt(testConfig.publicKey)
            val password = "password123".encrypt(testConfig.publicKey)

            val user = User(
                email = email,
                password = password,
                tosAccept = LocalDateTime.MIN,
                privacyAccept = LocalDateTime.MIN
            )
            registerUser(testConfig, user)

            test {
                val regResponse = it.post(
                    "http://localhost:80/" +
                            "${AuthRoute.AuthRouteVersion.V1.route}/" +
                            AuthRoute.Login.route
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(user)
                }
                val tokens = regResponse.body<AuthState.Success>()
                val userResponse =
                    it.get(
                        "http://localhost:80/" +
                                AuthRoute.AuthRouteVersion.V1.route + "/" +
                                AuthRoute.User.route
                    ) {
                        headers {
                            set("Authorization", "Bearer ${tokens.jwt}")
                        }
                    }
                assertEquals(userResponse.status, HttpStatusCode.OK)
            }
        }
    }

}