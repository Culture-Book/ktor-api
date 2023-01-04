package uk.co.culturebook.modules.authentication.data.interfaces

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

sealed interface AuthState {
    @Serializable
    data class Success(
        val jwt: String,
        @Serializable(with = UUIDSerializer::class) val refreshJwt: UUID
    ) : AuthState

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