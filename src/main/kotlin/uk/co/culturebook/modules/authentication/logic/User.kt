package uk.co.culturebook.modules.authentication.logic

import uk.co.culturebook.config.AppConfig
import uk.co.culturebook.config.getProperty
import uk.co.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import uk.co.culturebook.modules.authentication.data.models.interfaces.JwtClaim
import uk.co.culturebook.modules.authentication.data.models.interfaces.UserDetailsState
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*
import java.time.LocalDateTime

suspend fun getUserDetails(userId: String): UserDetailsState {
    val user = UserRepository.getUser(userId)?.copy(password = "") ?: return UserDetailsState.Error.Generic
    val tosDate = LocalDateTime.parse(AppConfig.TosDate.getProperty())
    val privacyDate = LocalDateTime.parse(AppConfig.PrivacyDate.getProperty())

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