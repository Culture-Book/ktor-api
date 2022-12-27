package sig.g.modules.authentication.data.models.states

import kotlinx.serialization.Serializable
import sig.g.modules.authentication.data.serializers.AuthErrorSerializer


@Serializable(with = AuthErrorSerializer::class)
enum class AuthError(val code: String) : AuthState {
    InvalidEmail("0001"),
    DuplicateEmail("0002"),
    DatabaseError("0003"),
    InvalidArgumentError("0004"),
    InvalidPassword("0005"),
    AuthenticationError("0006");

    companion object {
        fun parse(string: String): AuthError = when (string) {
            "0001" -> InvalidEmail
            "0002" -> DuplicateEmail
            "0003" -> DatabaseError
            "0004" -> InvalidArgumentError
            else -> throw IllegalArgumentException("This string doesn't have correspond to any AuthError")
        }
    }
}

