package sig.g

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.data_access.databaseConnection
import sig.g.plugins.*

fun main() {
    embeddedServer(Netty, port = AppConfig.AppPort.getProperty().toInt(), host = AppConfig.AppHost.getProperty()) {
        databaseConnection()
        configureSockets()
        configureSerialization()
        configureMonitoring()
        configureHTTP()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
