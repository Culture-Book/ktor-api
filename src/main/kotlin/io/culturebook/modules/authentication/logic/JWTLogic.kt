package io.culturebook.modules.authentication.logic

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.modules.authentication.constants.AuthRoute
import sig.g.modules.authentication.data.models.UserToken
import sig.g.modules.authentication.data.models.database.data_access.UserTokenRepository
import sig.g.modules.authentication.data.models.interfaces.JwtClaim
import sig.g.modules.authentication.data.models.interfaces.AuthState
import sig.g.modules.authentication.generateAccessJwt
import sig.g.modules.authentication.jwtVerifier

fun AuthenticationConfig.configureJwt() {
    jwt(AuthRoute.JwtAuth.route) {
        realm = AppConfig.JWTConfig.Realm.getProperty()
        verifier(jwtVerifier)

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

suspend fun refreshToken(userToken: UserToken?): AuthState {
    userToken ?: return AuthState.Error.AuthenticationError

    val newToken = generateUserToken(userId = userToken.userId)
    val isSuccess = UserTokenRepository.updateToken(newToken)

    return if (isSuccess) {
        val jwt = generateAccessJwt(newToken.userId, newToken.accessToken)
            ?: return AuthState.Error.Generic
        val refreshJwt = newToken.refreshToken
        AuthState.Success(jwt, refreshJwt!!)
    } else {
        AuthState.Error.DatabaseError
    }
}