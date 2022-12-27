package sig.g.modules.authentication.logic

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*
import sig.g.modules.authentication.data.UserRepository
import sig.g.modules.authentication.data.models.JwtClaim
import sig.g.modules.authentication.data.models.User

suspend fun getUserDetails(userId: String): User? {
    return UserRepository.getUser(userId)?.copy(password = "")
}

fun <T: Any> PipelineContext<T, ApplicationCall>.getUserId(): String {
    val principal = call.principal<JWTPrincipal>()
    return principal?.payload?.getClaim(JwtClaim.UserId.claim)?.asString() ?: ""
}