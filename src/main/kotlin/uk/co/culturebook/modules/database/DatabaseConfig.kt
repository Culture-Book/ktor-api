package uk.co.culturebook.modules.database

import io.ktor.server.config.*

object DatabaseConfig {
    val ApplicationConfig.driver get() = property("ktor.database.driver").getString()
    val ApplicationConfig.url get() = property("ktor.database.url").getString()
    val ApplicationConfig.username get() = property("ktor.database.user").getString()
    val ApplicationConfig.password get() = property("ktor.database.password").getString()
    val ApplicationConfig.idleTimeout get() = property("ktor.database.timeout").getString()
    val ApplicationConfig.poolSize get() = property("ktor.database.pool").getString()
}