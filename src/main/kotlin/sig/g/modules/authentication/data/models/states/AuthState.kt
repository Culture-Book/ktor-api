package sig.g.modules.authentication.data.models.states

import kotlinx.serialization.Serializable
import sig.g.modules.authentication.data.models.UserSession

sealed interface AuthState {
    @Serializable
    data class AuthSuccess(val userSession: UserSession, val jwt: String) : AuthState
}