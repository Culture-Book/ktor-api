package sig.g.modules.authentication.data.models.states

import kotlinx.serialization.Serializable

sealed interface AuthState {
    @Serializable
    data class Success(val jwt: String) : AuthState

    @Serializable
    enum class Error : AuthState {
        Generic,
        InvalidEmail,
        DuplicateEmail,
        DatabaseError,
        InvalidPassword,
        AuthenticationError;
    }
}