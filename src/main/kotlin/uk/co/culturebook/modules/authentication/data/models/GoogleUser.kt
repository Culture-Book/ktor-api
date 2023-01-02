package uk.co.culturebook.modules.authentication.data.models

import uk.co.culturebook.modules.authentication.constants.enums.RegistrationStatus
import uk.co.culturebook.modules.authentication.encodeOAuth
import kotlinx.serialization.Serializable
import java.net.URI

//{
//  "id": "105117701001556461099",
//  "name": "George Sigalas (DracCusS)",
//  "given_name": "George",
//  "family_name": "Sigalas",
//  "picture": "https://lh3.googleusercontent.com/a/AEdFTp5xWbwe_H3Kg5jF3WpIarhtmoUUNAiSkHLABqSGXA=s96-c",
//  "locale": "en-GB"
//}

@Serializable
data class GoogleUser(
    val id: String,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String,
    val locale: String,
    val email: String,
    val verified_email: Boolean
) {
    fun toUser(): User = User(
        userId = id,
        profileUri = URI(picture),
        displayName = name,
        email = email.encodeOAuth()!!,
        password = id.encodeOAuth()!!,
        registrationStatus = if (verified_email) RegistrationStatus.Registered.ordinal else RegistrationStatus.Pending.ordinal
    )
}