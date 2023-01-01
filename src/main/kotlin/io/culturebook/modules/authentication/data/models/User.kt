package io.culturebook.modules.authentication.data.models

import kotlinx.serialization.Serializable
import sig.g.data_access.serializers.LocalDateTimeSerializer
import sig.g.data_access.serializers.URISerializer
import sig.g.modules.authentication.constants.enums.RegistrationStatus
import sig.g.modules.authentication.constants.enums.VerificationStatus
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