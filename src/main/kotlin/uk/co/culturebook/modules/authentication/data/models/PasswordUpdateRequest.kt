package uk.co.culturebook.modules.authentication.data.models

@kotlinx.serialization.Serializable
data class PasswordUpdateRequest(
    val oldPassword: String,
    val newPassword: String,
)
