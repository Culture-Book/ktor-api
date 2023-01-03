package uk.co.culturebook.modules.email.data

import kotlinx.serialization.Serializable
import uk.co.culturebook.data_access.serializers.LocalDateTimeSerializer
import uk.co.culturebook.data_access.serializers.UUIDSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class PasswordResetRequest(
    @Serializable(with = UUIDSerializer::class)
    val passwordResetToken: UUID = UUID.randomUUID(),
    val userId: String = "",
    val email: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val expiresAt: LocalDateTime? = null
)