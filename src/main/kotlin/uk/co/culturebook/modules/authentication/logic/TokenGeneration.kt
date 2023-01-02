package uk.co.culturebook.modules.authentication.logic

import com.auth0.jwt.JWT
import uk.co.culturebook.modules.authentication.data.models.UserToken
import uk.co.culturebook.modules.authentication.data.models.interfaces.AuthState
import uk.co.culturebook.modules.authentication.data.models.interfaces.JwtClaim
import uk.co.culturebook.modules.utils.toUUID
import java.util.*

fun generateUserToken(userId: String): UserToken {
    val accessToken = UUID.randomUUID()
    val refreshToken = UUID.randomUUID()

    return UserToken(userId = userId, accessToken = accessToken, refreshToken = refreshToken)
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
