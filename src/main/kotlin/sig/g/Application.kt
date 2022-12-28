package sig.g

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.data_access.configureDatabase
import sig.g.modules.authentication.configureSecurity
import sig.g.plugins.*

val httpClient = HttpClient(Java) {
    engine {
        // this: JavaHttpConfig
        threadsCount = 8
        pipelining = true
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }
}

fun main() {
    embeddedServer(Netty, port = AppConfig.AppPort.getProperty().toInt(), host = "127.0.0.1") {
        configureDatabase()
        configureSockets()
        configureSerialization()
        configureMonitoring()
        configureHTTP()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
