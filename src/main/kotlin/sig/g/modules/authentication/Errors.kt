package sig.g.modules.authentication

import kotlinx.serialization.Serializable
import sig.g.data_access.serializers.AuthErrorSerializer

@Serializable(with = AuthErrorSerializer::class)
sealed interface AuthError {
    val code: String

    @Serializable
    object InvalidEmail : AuthError {
        override val code = "0001"
    }

    @Serializable

    object DuplicateEmail : AuthError {
        override val code = "0002"
    }

    @Serializable
    object DatabaseError : AuthError {
        override val code = "0003"
    }

    companion object {
        fun parse(string: String): AuthError = when (string) {
            "0001" -> InvalidEmail
            "0002" -> DuplicateEmail
            "0003" -> DatabaseError
            else -> throw IllegalArgumentException("This string doesn't have correspond to any AuthError")
        }
    }
}

