package io.culturebook.modules.authentication.logic

import com.auth0.jwt.JWT
import io.culturebook.modules.authentication.data.models.UserToken
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.culturebook.modules.authentication.data.models.interfaces.JwtClaim
import io.culturebook.modules.utils.toUUID
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
