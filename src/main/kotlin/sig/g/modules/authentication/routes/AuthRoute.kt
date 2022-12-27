package sig.g.modules.authentication.routes

sealed interface AuthRoute {
    val route: String

    object GoogleAuth : AuthRoute {
        override val route: String = "google-auth"
    }

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

    object User : AuthRoute {
        override val route: String = "user"
    }

    object JwtPublicKey : AuthRoute {
        override val route: String = ".well-known/jwks.json"
    }

    object OauthPublicKey : AuthRoute {
        override val route: String = ".well-known/oauth/public"
    }

    object GoogleLogin : AuthRoute {
        override val route: String = "google-login"
    }

    object GoogleUser : AuthRoute {
        override val route: String = "https://www.googleapis.com/oauth2/v1/userinfo?access_token="
    }

    object GoogleCallback : AuthRoute {
        override val route: String = "google-callback"
    }
}