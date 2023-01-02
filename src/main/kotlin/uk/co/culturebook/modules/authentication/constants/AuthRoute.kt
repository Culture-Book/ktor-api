package uk.co.culturebook.modules.authentication.constants

sealed interface AuthRoute {
    val route: String

    object JwtAuth : AuthRoute {
        override val route: String = "jwt-auth"
    }

    object JwtRefresh : AuthRoute {
        override val route: String = "jwt-refresh"
    }

    object Register : AuthRoute {
        override val route: String = "register"
    }

    object Login : AuthRoute {
        override val route: String = "login"
    }

    object RegisterOrLogin : AuthRoute {
        override val route: String = "register-login"
    }

    object User : AuthRoute {
        override val route: String = "user"

        object Tos : AuthRoute {
            override val route: String = "tos"
        }

        object Privacy : AuthRoute {
            override val route: String = "privacy"
        }
    }

    object JwtPublicKey : AuthRoute {
        override val route: String = ".well-known/jwks.json"
    }

    object OauthPublicKey : AuthRoute {
        override val route: String = ".well-known/oauth/public"
    }

    sealed interface AuthRouteVersion : AuthRoute {
        object V1 : AuthRouteVersion {
            override val route: String = "auth/v1"
        }
    }
}