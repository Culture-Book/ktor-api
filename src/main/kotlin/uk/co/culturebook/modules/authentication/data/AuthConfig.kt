package uk.co.culturebook.modules.authentication.data

import io.ktor.server.config.*

object AuthConfig {
    val ApplicationConfig.toSDate get() = property("ktor.generic.tos_date").getString()
    val ApplicationConfig.privacyDate get() = property("ktor.generic.privacy_date").getString()
    val ApplicationConfig.toSLink get() = property("ktor.generic.tos_link").getString()
    val ApplicationConfig.privacyLink get() = property("ktor.generic.privacy_link").getString()
    val ApplicationConfig.assetLink get() = property("ktor.generic.asset_link").getString()
    val ApplicationConfig.issuer get() = property("ktor.jwt.issuer").getString()
    val ApplicationConfig.realm get() = property("ktor.jwt.realm").getString()
    val ApplicationConfig.jwtExpiry get() = property("ktor.jwt.accessTokenExpiry").getString()
    val ApplicationConfig.privateKey get() = property("ktor.jwt.privateKey").getString()
    val ApplicationConfig.publicKey get() = property("ktor.jwt.publicKey").getString()
    val ApplicationConfig.emailHost get() = property("ktor.email.host").getString()
    val ApplicationConfig.smtpPort get() = property("ktor.email.smtpPort").getString()
    val ApplicationConfig.emailAccount get() = property("ktor.email.account").getString()
    val ApplicationConfig.emailPassword get() = property("ktor.email.password").getString()
    val ApplicationConfig.emailResetExpiry get() = property("ktor.email.passwordExpiry").getString()
}