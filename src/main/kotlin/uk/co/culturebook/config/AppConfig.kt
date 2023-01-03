package uk.co.culturebook.config

sealed interface AppConfig {
    val propertyKey: String

    object DatabaseDriver : AppConfig {
        override val propertyKey: String = "ktor.database.driver"
    }

    object TosDate : AppConfig {
        override val propertyKey: String = "ktor.application.tos_date"
    }

    object PrivacyDate : AppConfig {
        override val propertyKey: String = "ktor.application.privacy_date"
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

        object Realm : JWTConfig {
            override val propertyKey: String = "ktor.jwt.realm"
        }

        object AccessTokenExpiry : JWTConfig {
            override val propertyKey: String = "ktor.jwt.accessTokenExpiry"
        }

        object RefreshTokenExpiry : JWTConfig {
            override val propertyKey: String = "ktor.jwt.refreshTokenExpiry"
        }
    }

    sealed interface OAuthConfig : AppConfig {
        object PrivateKey : JWTConfig {
            override val propertyKey: String = "ktor.oauth.privateKey"
        }

        object PublicKey : JWTConfig {
            override val propertyKey: String = "ktor.oauth.publicKey"
        }
    }

    sealed interface EmailConfig : AppConfig {
        object Host : EmailConfig {
            override val propertyKey: String = "ktor.email.host"
        }

        object SmtpPort : EmailConfig {
            override val propertyKey: String = "ktor.email.smtpPort"
        }

        object Account : EmailConfig {
            override val propertyKey: String = "ktor.email.account"
        }

        object Password : EmailConfig {
            override val propertyKey: String = "ktor.email.password"
        }

        object PasswordResetExpiry : EmailConfig {
            override val propertyKey: String = "ktor.email.passwordExpiry"
        }

    }
}