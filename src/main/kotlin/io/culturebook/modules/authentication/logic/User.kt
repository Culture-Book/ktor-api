package io.culturebook.modules.authentication.logic

import io.culturebook.config.AppConfig
import io.culturebook.config.getProperty
import io.culturebook.modules.authentication.data.models.database.data_access.UserRepository
import io.culturebook.modules.authentication.data.models.interfaces.JwtClaim
import io.culturebook.modules.authentication.data.models.interfaces.UserDetailsState
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