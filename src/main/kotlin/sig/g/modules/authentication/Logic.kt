package sig.g.modules.authentication

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import sig.g.config.AppConfig
import sig.g.config.getListProperty
import sig.g.config.getProperty

private val googleProvider =
    OAuthServerSettings.OAuth2ServerSettings(
        name = AppConfig.GoogleSignIn.Name.getProperty(),
        authorizeUrl = AppConfig.GoogleSignIn.AuthorizeUrl.getProperty(),
        accessTokenUrl = AppConfig.GoogleSignIn.AccessTokenUrl.getProperty(),
        requestMethod = HttpMethod.Post,
        clientId = AppConfig.GoogleSignIn.CLientId.getProperty(),
        clientSecret = AppConfig.GoogleSignIn.ClientSecret.getProperty(),
        defaultScopes = AppConfig.GoogleSignIn.DefaultScopes.getListProperty()
    )

fun Application.configureSecurity() {
    // JWT validation and origin registration/login

    authentication {
        jwt {

        }

        oauth("auth-oauth-google") {
            urlProvider = { "${AppConfig.AppHost.getProperty()}/callback" }
            providerLookup = { googleProvider }
            client = HttpClient(Apache)
        }
    }

    routing {
        originAuth()
        googleOauth()
    }
}
