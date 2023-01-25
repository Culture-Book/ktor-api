package uk.co.culturebook

object Constants {
    fun forgotPasswordAppLink(email: String, passwordResetToken: String) =
        "https://api.culturebook.co.uk/forgot/$email/$passwordResetToken"

    const val WellKnownRoute = ".well-known"

    object Headers {
        const val Bearer = "Bearer"
        const val Authorization = "Authorization"
        const val ApiKey = "apikey"
    }
}