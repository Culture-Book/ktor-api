package sig.g.modules.authentication.logic

import sig.g.modules.authentication.data.UserRepository
import sig.g.modules.authentication.data.UserRepository.exists
import sig.g.modules.authentication.data.UserTokenRepository
import sig.g.modules.authentication.data.models.User
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.decodeOAuth
import sig.g.modules.authentication.generateAccessJwt
import java.util.*

suspend fun registerUser(callUser: User): AuthState {
    val decryptedEmail = callUser.email.decodeOAuth()
    val decryptedPassword = callUser.password.decodeOAuth()

    if (decryptedEmail.isNullOrEmpty() || !decryptedEmail.isProperEmail()) {
        return AuthState.Error.InvalidEmail
    }

    if (decryptedPassword.isNullOrEmpty()) {
        return AuthState.Error.InvalidPassword
    }

    if (decryptedEmail.exists()) {
        return AuthState.Error.DuplicateEmail
    }

    val user = User(
        userId = callUser.userId.ifBlank { UUID.randomUUID().toString() },
        profileUri = callUser.profileUri,
        displayName = callUser.displayName,
        password = callUser.password,
        email = decryptedEmail,
        verificationStatus = callUser.verificationStatus,
        registrationStatus = callUser.registrationStatus
    )
    val dbUser =
        UserRepository.registerUser(user) ?: return AuthState.Error.DatabaseError

    val userToken = generateUserToken(dbUser.userId)
    val jwt = generateAccessJwt(dbUser.userId, userToken.accessToken, userToken.refreshToken)
        ?: return AuthState.Error.Generic
    UserTokenRepository.insertToken(userToken) ?: return AuthState.Error.Generic

    return AuthState.Success(jwt)
}