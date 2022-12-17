package sig.g.data_access

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.exceptions.DatabaseNotInitialised

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

fun Application.attemptDatabaseConnection(): Database? =
    try {
        Database.connect(hikariConfig)
    } catch (e: Exception) {
        throw DatabaseNotInitialised(e.message)
    }

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }