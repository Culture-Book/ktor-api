package sig.g.modules.authentication

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import kotlin.collections.set

private val googleProvider =
    OAuthServerSettings.OAuth2ServerSettings(
        name = "google",
        authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
        accessTokenUrl = "https://oauth2.googleapis.com/token",
        requestMethod = HttpMethod.Post,
        clientId = System.getenv("GOOGLE_CLIENT_ID"),
        clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
        defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
    )

fun Application.configureSecurity() {
    // JWT validation and origin registration/login

    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "${AppConfig.AppHost.getProperty()}/callback" }
            providerLookup = { googleProvider }
            client = HttpClient(Apache)
        }
    }

    install(Sessions) {
        cookie<UserSession>("UserSession") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    routing {
        googleOauth()
    }
}

