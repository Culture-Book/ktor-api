package uk.co.culturebook

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import uk.co.culturebook.config.AppConfig
import uk.co.culturebook.config.getProperty
import uk.co.culturebook.data_access.configureDatabase
import uk.co.culturebook.modules.authentication.configureSecurity
import uk.co.culturebook.plugins.*

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
