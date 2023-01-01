package io.culturebook.modules.authentication.data.models.interfaces

import kotlinx.serialization.Serializable
import sig.g.data_access.serializers.UUIDSerializer
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