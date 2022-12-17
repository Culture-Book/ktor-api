package sig.g.di

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.Database
import sig.g.data_access.attemptDatabaseConnection
import sig.g.exceptions.DatabaseNotInitialised

object Singletons {
    val appConfig by lazy { HoconApplicationConfig(ConfigFactory.load()) }
    private var database : Database? = null

    @Throws(DatabaseNotInitialised::class)
    fun Application.getDatabase() = attemptDatabaseConnection()?.also { db -> database = db }

    @TestOnly
    fun setDatabase(db : Database) {
        database = db
    }
}