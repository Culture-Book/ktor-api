package sig.g.config

sealed interface AppConfig {
    val propertyKey: String

    object DatabaseDriver : AppConfig {
        override val propertyKey: String = "ktor.database.driver"
    }

    object DatabaseUser : AppConfig {
        override val propertyKey: String = "ktor.database.url"
    }

    object DatabasePassword : AppConfig {
        override val propertyKey: String = "ktor.database.user"
    }

    object DatabaseUrl : AppConfig {
        override val propertyKey: String = "ktor.database.password"
    }

    object AppHost : AppConfig {
        override val propertyKey: String = "ktor.database.app_host"
    }

    object AppPort : AppConfig {
        override val propertyKey: String = "ktor.database.app_port"
    }
}