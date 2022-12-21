package sig.g.modules.authentication

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sig.g.config.AppConfig
import sig.g.config.getListProperty
import sig.g.config.getProperty
import sig.g.modules.authentication.data.JwtClaim
import sig.g.modules.authentication.data.UserDAOFacadeImpl.exists
import sig.g.modules.utils.toUUID

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
        jwt("auth-jwt") {
            realm = AppConfig.JWTConfig.Realm.getProperty()
            val issuer = AppConfig.JWTConfig.Issuer.getProperty()
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }

            validate { jwtCredential: JWTCredential ->
                val userIdClaim = jwtCredential.payload
                    .getClaim(JwtClaim.UserId.claim)
                    .asString()
                    .toUUID()

                if (userIdClaim.exists()) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
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

fun String?.isProperEmail() = Regex("^([a-zA-Z0-9_\\-]+)@([a-zA-Z0-9_\\-]+)\\.([a-zA-Z]{2,5})\$").matches(this ?: "")
