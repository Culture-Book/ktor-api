package uk.co.culturebook.modules.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import org.intellij.lang.annotations.Language
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.culturebook.modules.authentication.data.database.tables.PasswordResets
import uk.co.culturebook.modules.authentication.data.database.tables.UserTokens
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.culture.add_new.location.data.database.tables.Cultures
import uk.co.culturebook.modules.database.DatabaseConfig.driver
import uk.co.culturebook.modules.database.DatabaseConfig.idleTimeout
import uk.co.culturebook.modules.database.DatabaseConfig.password
import uk.co.culturebook.modules.database.DatabaseConfig.poolSize
import uk.co.culturebook.modules.database.DatabaseConfig.url
import uk.co.culturebook.modules.database.DatabaseConfig.username
import java.sql.ResultSet
import kotlin.coroutines.CoroutineContext

private val ApplicationConfig.hikariConfig
    get() = HikariDataSource(
        HikariConfig().also {
            it.driverClassName = driver
            it.jdbcUrl = url
            it.username = username
            it.password = password
            it.idleTimeout = idleTimeout.toLong()
            it.maximumPoolSize = poolSize.toInt()
            it.isAutoCommit = false
            it.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            it.validate()
        })

fun Application.databaseModule() =
    try {
        val db = Database.connect(environment.config.hikariConfig)
        transaction(db) {
            SchemaUtils.create(Cultures)
            SchemaUtils.create(Users)
            SchemaUtils.create(UserTokens)
            SchemaUtils.create(PasswordResets)
            getDistanceFunction()
            similarityFunction()
        }
        db
    } catch (e: Exception) {
        throw DatabaseNotInitialised(e.message)
    }

suspend fun <T> dbQuery(coroutineContext: CoroutineContext? = null, block: suspend () -> T): T =
    newSuspendedTransaction(context = coroutineContext ?: Dispatchers.IO) { block() }

suspend fun <T : Any> rawQuery(
    @Language("sql") query: String,
    transform: (ResultSet) -> T
): T? = dbQuery(currentCoroutineContext()) {
    TransactionManager.current().exec(query, transform = transform)
}