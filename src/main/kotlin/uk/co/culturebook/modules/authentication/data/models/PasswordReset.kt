package uk.co.culturebook.modules.authentication.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class PasswordReset(
    val userId: String,
    @Serializable(with = UUIDSerializer::class)
    val token: UUID,
    val password: String
)