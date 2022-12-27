package sig.g.modules.authentication.data.models.states

import kotlinx.serialization.Serializable

sealed interface AuthState {
    @Serializable
    data class AuthSuccess(val jwt: String) : AuthState
}