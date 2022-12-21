package sig.g

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import sig.g.data_access.configureDatabase
import sig.g.modules.authentication.configureSecurity
import sig.g.plugins.*

abstract class BaseApplicationTest {
    private var database: Database? = null

    @Before
    fun prepare() {
        System.setProperty("test", "true")
    }

    @KtorDsl
    fun testApp(block: suspend ApplicationTestBuilder.(testClient: io.ktor.client.HttpClient) -> Unit) {
        System.setProperty("test", "true")


        testApplication {
            application {
                database = configureDatabase()
                configureSockets()
                configureSerialization()
                configureMonitoring()
                configureHTTP()
                configureSecurity()
                configureRouting()
            }

            val testClient = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

            block(testClient)
        }
    }

    @After
    fun reset() {
        testApp {
            try {
                transaction(database) {
                    exec("DROP TABLE users;")
                }
            } catch (_: Exception) {
            }

        }
    }
}