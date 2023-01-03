package uk.co.culturebook.modules.email.data

import kotlinx.serialization.Serializable
import uk.co.culturebook.data_access.serializers.UUIDSerializer
import java.util.*

@Serializable
data class PasswordReset(
    val email: String,
    @Serializable(with = UUIDSerializer::class)
    val token: UUID,
    val password: String
)