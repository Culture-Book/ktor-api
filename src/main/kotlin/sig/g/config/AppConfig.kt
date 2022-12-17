package sig.g.config

sealed interface AppConfig {
    val propertyKey: String

    object DatabaseDriver : AppConfig {
        override val propertyKey: String = "ktor.database.driver"
    }

    object DatabaseUser : AppConfig {
        override val propertyKey: String = "ktor.database.user"
    }

    object DatabasePassword : AppConfig {
        override val propertyKey: String = "ktor.database.password"
    }

    object DatabaseUrl : AppConfig {
        override val propertyKey: String = "ktor.database.url"
    }

    object AppHost : AppConfig {
        override val propertyKey: String = "ktor.application.app_host"
    }

    object AppPort : AppConfig {
        override val propertyKey: String = "ktor.application.app_port"
    }

    object DatabaseIdleTimeout : AppConfig {
        override val propertyKey: String = "ktor.database.timeout"
    }

    object DatabasePoolSize : AppConfig {
        override val propertyKey: String = "ktor.database.pool"
    }
}