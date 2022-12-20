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

    sealed interface JWTConfig : AppConfig {
        object PrivateKey : JWTConfig {
            override val propertyKey: String = "ktor.jwt.privateKey"
        }

        object PublicKey : JWTConfig {
            override val propertyKey: String = "ktor.jwt.publicKey"
        }

        object Issuer : JWTConfig {
            override val propertyKey: String = "ktor.jwt.issuer"
        }

        object Audience : JWTConfig {
            override val propertyKey: String = "ktor.jwt.audience"
        }

        object Realm : JWTConfig {
            override val propertyKey: String = "ktor.jwt.realm"
        }
    }

    sealed interface GoogleSignIn : AppConfig {

        object Name : GoogleSignIn {
            override val propertyKey: String = "ktor.google.name"
        }

        object AuthorizeUrl : GoogleSignIn {
            override val propertyKey: String = "ktor.google.authorizeUrl"
        }

        object AccessTokenUrl : GoogleSignIn {
            override val propertyKey: String = "ktor.google.accessTokenUrl"
        }

        object CLientId : GoogleSignIn {
            override val propertyKey: String = "ktor.google.clientId"
        }

        object ClientSecret : GoogleSignIn {
            override val propertyKey: String = "ktor.google.clientSecret"
        }

        object DefaultScopes : GoogleSignIn {
            override val propertyKey: String = "ktor.google.defaultScopes"
        }
    }
}