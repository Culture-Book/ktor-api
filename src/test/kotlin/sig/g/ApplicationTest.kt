package sig.g

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.serialization.gson.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*
import io.ktor.server.request.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.locations.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import sig.g.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}