package sig.g.di

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object Singletons {
    val appConfig by lazy { HoconApplicationConfig(ConfigFactory.load()) }
}