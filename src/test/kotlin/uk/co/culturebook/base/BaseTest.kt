package uk.co.culturebook.base

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.After
import org.junit.Before
import uk.co.culturebook.modules.authentication.data.AuthConfig.publicKey
import uk.co.culturebook.modules.authentication.data.database.tables.PasswordResets
import uk.co.culturebook.modules.authentication.data.database.tables.UserTokens
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.culture.data.database.tables.*
import uk.co.culturebook.modules.culture.data.database.tables.contribution.*
import uk.co.culturebook.modules.culture.data.database.tables.element.*
import uk.co.culturebook.modules.database.getDistanceFunction
import uk.co.culturebook.modules.database.similarityFunction
import uk.co.culturebook.modules.http.ClientFactory
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.util.*
import javax.crypto.Cipher

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
abstract class BaseTest {
    val testConfig = ApplicationConfig("./src/test/kotlin/application.conf")

    private fun generateJavaPublicKey(publicKey: String): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeyDecoded = Base64.getDecoder().decode(publicKey)
        val publicEncodedKeySpec = X509EncodedKeySpec(publicKeyDecoded)
        return keyFactory.generatePublic(publicEncodedKeySpec)
    }

    fun String.encrypt(key: String): String {
        val encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING").apply {
            init(Cipher.ENCRYPT_MODE, generateJavaPublicKey(key))
        }
        val stringBytes = toByteArray(Charsets.ISO_8859_1)
        val secretBytes = encryptCipher.doFinal(stringBytes)

        return String(Base64.getEncoder().encode(secretBytes), StandardCharsets.ISO_8859_1)
    }

    fun testAuthenticated(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) {
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
            val jwt = regResponse.body<AuthState.Success>().jwt
            it.plugin(HttpSend).intercept { oldRequest ->
                oldRequest.headers {
                    append("Authorization", "Bearer $jwt")
                }
                execute(oldRequest)
            }
            block(it)
        }
    }

    fun test(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) {
        testApplication {
            environment {
                config = testConfig
            }
            val client = createClient {
                install(HttpCookies)
                install(WebSockets)
                install(DefaultRequest) {
                    contentType(ContentType.Application.Json)
                }
                install(ContentNegotiation) {
                    json()
                }
            }
            testSuspend { block(client) }
        }
    }

    // Set up a mock database
    private val dbUrl = "jdbc:h2:mem:test;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;"
    private val dbDriver = "org.h2.Driver"
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val dbConfig = HikariDataSource(HikariConfig().apply {
        jdbcUrl = dbUrl
        driverClassName = dbDriver
        connectionInitSql =
            "CREATE ALIAS IF NOT EXISTS MY_SIMILARITY DETERMINISTIC FOR 'uk.co.culturebook.utils.SearchUtilsKt.matchStrings';" +
                    "CREATE ALIAS IF NOT EXISTS DISTANCE_IN_KM DETERMINISTIC FOR 'uk.co.culturebook.utils.DistanceUtilsKt.getDistanceInKm';"
        validate()
    })

    // Set up a mock client
    private val testClient = TestHttpEngine(testConfig)

    @Before
    open fun setUp() {
        //Set up the http client
        ClientFactory.getInstance(testClient)

        // Set up the database
        val db = Database.connect(datasource = dbConfig)
        Dispatchers.setMain(mainThreadSurrogate)
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                connection.autoCommit = true

                // Auth tables
                SchemaUtils.create(Cultures)
                SchemaUtils.create(Users)
                SchemaUtils.create(UserTokens)
                SchemaUtils.create(PasswordResets)

                // Element Tables
                SchemaUtils.create(Elements)
                SchemaUtils.create(Contributions)
                SchemaUtils.create(Media)
                SchemaUtils.create(BlockedElementComments)
                SchemaUtils.create(BlockedContributionComments)

                // M-M tables
                SchemaUtils.create(LinkedElements)
                SchemaUtils.create(ElementComments)
                SchemaUtils.create(ElementReactions)
                SchemaUtils.create(ElementMedia)
                SchemaUtils.create(LinkedContributions)
                SchemaUtils.create(ContributionComments)
                SchemaUtils.create(ContributionReactions)
                SchemaUtils.create(ContributionMedia)
                SchemaUtils.create(BlockedElements, BlockedCultures, BlockedContributions)
                SchemaUtils.create(FavouriteElements, FavouriteCultures, FavouriteContributions)

                // Custom functions
                getDistanceFunction()
                similarityFunction()

                connection.autoCommit = false
            }
        }
    }

    @After
    fun tearDown() {
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                connection.autoCommit = true
                SchemaUtils.sortTablesByReferences(
                    listOf(
                        // Auth tables
                        Cultures,
                        Users,
                        UserTokens,
                        PasswordResets,

                        // Element Tables
                        Elements,
                        Contributions,
                        Media,
                        BlockedElementComments,
                        BlockedContributionComments,

                        // M-M tables
                        LinkedElements,
                        ElementComments,
                        ElementReactions,
                        ElementMedia,
                        LinkedContributions,
                        ContributionComments,
                        ContributionReactions,
                        ContributionMedia,
                        BlockedElements,
                        BlockedCultures,
                        BlockedContributions,
                        FavouriteElements,
                        FavouriteCultures,
                        FavouriteContributions
                    )
                )
                    .reversed()
                    .forEach { SchemaUtils.drop(it) }
                connection.autoCommit = false
            }
        }
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}