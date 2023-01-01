package io.culturebook

import io.culturebook.config.AppConfig
import io.culturebook.config.getProperty
import io.culturebook.data_access.configureDatabase
import io.culturebook.modules.authentication.configureSecurity
import io.culturebook.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

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
