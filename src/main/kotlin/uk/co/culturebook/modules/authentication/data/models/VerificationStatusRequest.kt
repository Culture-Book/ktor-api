package uk.co.culturebook.modules.authentication.data.models

import kotlinx.serialization.Serializable

@Serializable
data class VerificationStatusRequest(
    val reason: String,
)
