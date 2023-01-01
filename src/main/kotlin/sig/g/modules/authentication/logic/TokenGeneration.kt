package sig.g.modules.authentication.logic

import com.auth0.jwt.JWT
import io.ktor.server.auth.jwt.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.modules.authentication.data.models.JwtClaim
import sig.g.modules.authentication.data.models.UserToken
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.utils.addSeconds
import sig.g.modules.utils.toUUID
import java.util.*

fun generateUserToken(userId: String): UserToken {
    val accessToken = UUID.randomUUID()

    return UserToken(userId = userId, accessToken = accessToken)
}

fun JWTPrincipal.getUserToken(): UserToken {
    val accessClaim = payload
        .getClaim(JwtClaim.AccessToken.claim)
        .asString()
        .toUUID()
    val userIdClaim = payload
        .getClaim(JwtClaim.UserId.claim)
        .asString()

    return UserToken(userId = userIdClaim, accessToken = accessClaim)
}

fun AuthState.Success.getUserToken(): UserToken {
    val decodedJWT = JWT.decode(jwt)
    val accessClaim = decodedJWT
        .getClaim(JwtClaim.AccessToken.claim)
        .asString()
        .toUUID()
    val userIdClaim = decodedJWT
        .getClaim(JwtClaim.UserId.claim)
        .asString()

    return UserToken(userId = userIdClaim, accessToken = accessClaim)
}

fun JWTCredential.getUserToken(): UserToken {
    val accessClaim = payload
        .getClaim(JwtClaim.AccessToken.claim)
        .asString()
        .toUUID()
    val userIdClaim = payload
        .getClaim(JwtClaim.UserId.claim)
        .asString()

    return UserToken(userId = userIdClaim, accessToken = accessClaim)
}