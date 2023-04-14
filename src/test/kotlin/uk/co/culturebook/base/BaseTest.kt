package uk.co.culturebook.base

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
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
import uk.co.culturebook.modules.authentication.data.database.tables.PasswordResets
import uk.co.culturebook.modules.authentication.data.database.tables.UserTokens
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.culture.data.database.tables.*
import uk.co.culturebook.modules.culture.data.database.tables.contribution.*
import uk.co.culturebook.modules.culture.data.database.tables.element.*
import uk.co.culturebook.modules.database.getDistanceFunction
import uk.co.culturebook.modules.database.similarityFunction
import uk.co.culturebook.modules.http.ClientFactory

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
abstract class BaseTest {
    val testConfig = ApplicationConfig("./src/test/kotlin/application.conf")

    fun test(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) {
        testApplication {
            environment {
                config = testConfig
            }
            val client = createClient {
                install(HttpCookies)
                install(WebSockets)
                install(ContentNegotiation) {
                    json()
                }
            }
            testSuspend { block(client) }
        }
    }

    // Set up a mock database
    private val dbUrl = "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;"
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