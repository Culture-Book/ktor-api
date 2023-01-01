package io.culturebook.modules.authentication.data.models.interfaces

import io.culturebook.data_access.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
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