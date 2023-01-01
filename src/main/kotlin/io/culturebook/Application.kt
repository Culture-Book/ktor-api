package io.culturebook

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.data_access.configureDatabase
import sig.g.modules.authentication.configureSecurity
import sig.g.plugins.*

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
