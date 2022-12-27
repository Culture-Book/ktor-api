package sig.g.modules.authentication.logic

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.modules.authentication.data.UserTokenRepository
import sig.g.modules.authentication.jwtVerifier
import sig.g.modules.authentication.routes.AuthRoute

fun AuthenticationConfig.configureJwt() {
    jwt(AuthRoute.JwtAuth.route) {
        realm = AppConfig.JWTConfig.Realm.getProperty()
        verifier(jwtVerifier)

        validate { jwtCredential: JWTCredential ->
            val userToken = jwtCredential.getUserToken()

            if (UserTokenRepository.userTokenExists(userToken)) {
                JWTPrincipal(jwtCredential.payload)
            } else {
                null
            }
        }

        challenge { _, _ ->
            call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
        }
    }
}