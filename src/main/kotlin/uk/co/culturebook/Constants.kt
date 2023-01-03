package uk.co.culturebook

object Constants {
    fun forgotPasswordAppLink(email: String, passwordResetToken: String) =
        "https://culturebook.co.uk/forgot/$email/$passwordResetToken"
}