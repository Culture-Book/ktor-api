package sig.g.data_access

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import sig.g.config.AppConfig
import sig.g.config.getProperty

private val hikariConfig = HikariDataSource(
    HikariConfig().apply {
        driverClassName = AppConfig.DatabaseDriver.getProperty()
        jdbcUrl = AppConfig.DatabaseUrl.getProperty()
        username = AppConfig.DatabaseUser.getProperty()
        password = AppConfig.DatabasePassword.getProperty()
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

fun Application.databaseConnection() {
    Database.connect(hikariConfig)
}