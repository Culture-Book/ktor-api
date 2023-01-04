package uk.co.culturebook.modules.authentication.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.authentication.data.enums.RegistrationStatus
import uk.co.culturebook.modules.authentication.data.enums.VerificationStatus
import uk.co.culturebook.modules.serialization.serializers.LocalDateTimeSerializer
import uk.co.culturebook.modules.serialization.serializers.URISerializer
import java.net.URI
import java.time.LocalDateTime

@Serializable
data class User(
    val userId: String = "",
    @Serializable(with = URISerializer::class)
    val profileUri: URI? = null,
    val displayName: String? = null,
    val password: String,
    val email: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val tosAccept: LocalDateTime? = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val privacyAccept: LocalDateTime? = LocalDateTime.now(),
    val verificationStatus: Int = VerificationStatus.NotVerified.ordinal,
    val registrationStatus: Int = RegistrationStatus.Pending.ordinal
)
