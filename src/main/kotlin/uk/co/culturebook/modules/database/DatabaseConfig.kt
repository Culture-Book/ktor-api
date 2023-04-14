package uk.co.culturebook.modules.database

import io.ktor.server.config.*

object DatabaseConfig {
    val ApplicationConfig.initSqlString get() = propertyOrNull("ktor.database.init")?.getString()
    val ApplicationConfig.driver get() = propertyOrNull("ktor.database.driver")?.getString()
    val ApplicationConfig.url get() = propertyOrNull("ktor.database.url")?.getString()
    val ApplicationConfig.username get() = propertyOrNull("ktor.database.user")?.getString()
    val ApplicationConfig.password get() = propertyOrNull("ktor.database.password")?.getString()
    val ApplicationConfig.idleTimeout get() = propertyOrNull("ktor.database.timeout")?.getString()
    val ApplicationConfig.poolSize get() = propertyOrNull("ktor.database.pool")?.getString()
}