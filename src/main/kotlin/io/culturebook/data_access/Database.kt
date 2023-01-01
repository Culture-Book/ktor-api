package io.culturebook.data_access

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.exceptions.DatabaseNotInitialised
import sig.g.modules.authentication.data.models.database.UserTokens
import sig.g.modules.authentication.data.models.database.Users

private val hikariConfig = HikariDataSource(
    HikariConfig().apply {
        driverClassName = AppConfig.DatabaseDriver.getProperty()
        jdbcUrl = AppConfig.DatabaseUrl.getProperty()
        username = AppConfig.DatabaseUser.getProperty()
        password = AppConfig.DatabasePassword.getProperty()
        idleTimeout = AppConfig.DatabaseIdleTimeout.getProperty().toLongOrNull() ?: 5000
        maximumPoolSize = AppConfig.DatabasePoolSize.getProperty().toIntOrNull() ?: 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

fun configureDatabase(): Database? =
    try {
        val db = Database.connect(culturebook.data_access.hikariConfig)
        transaction(db) {
            SchemaUtils.create(Users)
            SchemaUtils.create(UserTokens)
        }
        db
    } catch (e: Exception) {
        throw DatabaseNotInitialised(e.message)
    }

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }