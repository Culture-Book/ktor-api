package sig.g.modules.authentication.logic

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.auth.*
import sig.g.config.AppConfig
import sig.g.config.getListProperty
import sig.g.config.getProperty
import sig.g.modules.authentication.routes.AuthRoute

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

fun AuthenticationConfig.configureGoogleSignIn() {
    oauth(AuthRoute.GoogleAuth.route) {
        urlProvider = {
            "${AppConfig.AppHost.getProperty()}/${AuthRoute.GoogleCallback.route}"
        }
        providerLookup = { googleProvider }
        client = HttpClient(Apache)
    }
}