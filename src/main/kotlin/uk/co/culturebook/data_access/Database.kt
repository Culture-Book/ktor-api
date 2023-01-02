package uk.co.culturebook.data_access

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import uk.co.culturebook.config.AppConfig
import uk.co.culturebook.config.getProperty
import uk.co.culturebook.exceptions.DatabaseNotInitialised
import uk.co.culturebook.modules.authentication.data.models.database.UserTokens
import uk.co.culturebook.modules.authentication.data.models.database.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

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
        val db = Database.connect(hikariConfig)
        transaction(db) {
            SchemaUtils.create(Users)
            SchemaUtils.create(UserTokens)
        }
        db
    } catch (e: Exception) {
        throw DatabaseNotInitialised(e.message)
    }

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }