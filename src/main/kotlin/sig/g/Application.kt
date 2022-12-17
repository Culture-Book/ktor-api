package sig.g

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.plugins.*

fun main() {
    embeddedServer(Netty, port = AppConfig.AppPort.getProperty().toInt(), host = "0.0.0.0") {
        configureSockets()
        configureSerialization()
        configureMonitoring()
        configureHTTP()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
