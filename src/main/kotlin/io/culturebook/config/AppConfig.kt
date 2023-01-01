package io.culturebook.config

sealed interface AppConfig {
    val propertyKey: String

    object DatabaseDriver : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.database.driver"
    }

    object TosDate : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.application.tos_date"
    }

    object PrivacyDate : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.application.privacy_date"
    }

    object DatabaseUser : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.database.user"
    }

    object DatabasePassword : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.database.password"
    }

    object DatabaseUrl : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.database.url"
    }

    object AppHost : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.application.app_host"
    }

    object AppPort : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.application.app_port"
    }

    object DatabaseIdleTimeout : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.database.timeout"
    }

    object DatabasePoolSize : io.culturebook.config.AppConfig {
        override val propertyKey: String = "ktor.database.pool"
    }

    sealed interface JWTConfig : io.culturebook.config.AppConfig {
        object PrivateKey : io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.jwt.privateKey"
        }

        object PublicKey : _root_ide_package_.io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.jwt.publicKey"
        }

        object Issuer : _root_ide_package_.io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.jwt.issuer"
        }

        object Realm : _root_ide_package_.io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.jwt.realm"
        }

        object AccessTokenExpiry : _root_ide_package_.io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.jwt.accessTokenExpiry"
        }

        object RefreshTokenExpiry : _root_ide_package_.io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.jwt.refreshTokenExpiry"
        }
    }

    sealed interface OAuthConfig : _root_ide_package_.io.culturebook.config.AppConfig {
        object PrivateKey : _root_ide_package_.io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.oauth.privateKey"
        }

        object PublicKey : _root_ide_package_.io.culturebook.config.AppConfig.JWTConfig {
            override val propertyKey: String = "ktor.oauth.publicKey"
        }
    }

    sealed interface GoogleSignIn : _root_ide_package_.io.culturebook.config.AppConfig {

        object Name : _root_ide_package_.io.culturebook.config.AppConfig.GoogleSignIn {
            override val propertyKey: String = "ktor.google.name"
        }

        object AuthorizeUrl : _root_ide_package_.io.culturebook.config.AppConfig.GoogleSignIn {
            override val propertyKey: String = "ktor.google.authorizeUrl"
        }

        object AccessTokenUrl : _root_ide_package_.io.culturebook.config.AppConfig.GoogleSignIn {
            override val propertyKey: String = "ktor.google.accessTokenUrl"
        }

        object CLientId : _root_ide_package_.io.culturebook.config.AppConfig.GoogleSignIn {
            override val propertyKey: String = "ktor.google.clientId"
        }

        object ClientSecret : _root_ide_package_.io.culturebook.config.AppConfig.GoogleSignIn {
            override val propertyKey: String = "ktor.google.clientSecret"
        }

        object DefaultScopes : _root_ide_package_.io.culturebook.config.AppConfig.GoogleSignIn {
            override val propertyKey: String = "ktor.google.defaultScopes"
        }
    }
}