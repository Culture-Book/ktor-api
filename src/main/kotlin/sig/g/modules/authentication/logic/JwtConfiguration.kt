package sig.g.modules.authentication.logic

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.UserTokenRepository
import sig.g.modules.authentication.data.models.JwtClaim
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.jwtVerifier

fun AuthenticationConfig.configureJwt() {
    jwt(AuthRoute.JwtAuth.route) {
        realm = AppConfig.JWTConfig.Realm.getProperty()
        verifier(jwtVerifier)

        validate { jwtCredential: JWTCredential ->
            val jwtUserId = jwtCredential.payload.getClaim(JwtClaim.UserId.claim)?.asString() ?: ""

            // session token
            val authState = sessions.get(AuthState.AuthSuccess::class)
            val jwt = JWT.decode(authState?.jwt)
            val sessionUserId = jwt?.getClaim(JwtClaim.UserId.claim)?.asString() ?: ""
            val userToken = jwtCredential.getUserToken()

            if (UserTokenRepository.userTokenExists(userToken) && jwtUserId == sessionUserId) {
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