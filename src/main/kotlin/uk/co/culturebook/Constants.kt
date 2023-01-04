package uk.co.culturebook

object Constants {
    fun forgotPasswordAppLink(email: String, passwordResetToken: String) =
        "https://api.culturebook.co.uk/forgot/$email/$passwordResetToken"

    const val WellKnownRoute = ".well-known"
}