package uk.co.culturebook.modules.authentication.logic.general

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import uk.co.culturebook.modules.authentication.data.AuthConfig.realm
import uk.co.culturebook.modules.authentication.data.database.repositories.UserTokenRepository
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import uk.co.culturebook.modules.authentication.data.interfaces.JwtClaim
import uk.co.culturebook.modules.authentication.data.models.UserToken
import uk.co.culturebook.modules.authentication.generateAccessJwt
import uk.co.culturebook.modules.authentication.jwtVerifier
import uk.co.culturebook.modules.authentication.logic.utils.generateUserToken

fun AuthenticationConfig.configureJwt(config: ApplicationConfig) {
    jwt(AuthRoute.JwtAuth.route) {
        realm = config.realm
        verifier(config.jwtVerifier)

        validate { jwtCredential: JWTCredential ->
            val jwtUserId = jwtCredential.payload.getClaim(JwtClaim.UserId.claim)?.asString() ?: ""

            // session token
            val authState = sessions.get(AuthState.Success::class)
            val jwt = JWT.decode(authState?.jwt)
            val sessionUserId = jwt?.getClaim(JwtClaim.UserId.claim)?.asString() ?: ""

            if (jwtUserId == sessionUserId) {
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

suspend fun refreshToken(config: ApplicationConfig, userToken: UserToken?): AuthState {
    userToken ?: return AuthState.Error.AuthenticationError

    val newToken = generateUserToken(userId = userToken.userId)
    val isSuccess = UserTokenRepository.updateToken(newToken)

    return if (isSuccess) {
        val jwt = generateAccessJwt(config, newToken.userId, newToken.accessToken)
            ?: return AuthState.Error.Generic
        val refreshJwt = newToken.refreshToken
        AuthState.Success(jwt, refreshJwt!!)
    } else {
        AuthState.Error.DatabaseError
    }
}