package uk.co.culturebook.modules.authentication.logic.authenticated

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.interfaces.JwtClaim
import uk.co.culturebook.modules.authentication.data.interfaces.UserDetailsState
import java.time.LocalDateTime

suspend fun getUserDetails(userId: String, tosDate: LocalDateTime, privacyDate: LocalDateTime): UserDetailsState {
    val user = UserRepository.getUser(userId)?.copy(password = "") ?: return UserDetailsState.Error.Generic

    return if (tosDate.isAfter(user.tosAccept)) {
        UserDetailsState.Error.TosUpdate
    } else if (privacyDate.isAfter(user.privacyAccept)) {
        UserDetailsState.Error.PrivacyUpdate
    } else {
        UserDetailsState.Success(user)
    }
}

fun <T : Any> PipelineContext<T, ApplicationCall>.getUserId(): String {
    val principal = call.principal<JWTPrincipal>()
    return principal?.payload?.getClaim(JwtClaim.UserId.claim)?.asString() ?: ""
}