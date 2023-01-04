package uk.co.culturebook.modules.authentication.data.interfaces

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

    object OauthPublicKey : AuthRoute {
        override val route: String = ".well-known/oauth/public"
    }

    object ResetPassword : AuthRoute {
        override val route = "reset-password"
    }

    object ForgotPassword : AuthRoute {
        override val route = "forgot"
    }

    sealed interface AuthRouteVersion : AuthRoute {
        object V1 : AuthRouteVersion {
            override val route: String = "auth/v1"
        }
    }
}