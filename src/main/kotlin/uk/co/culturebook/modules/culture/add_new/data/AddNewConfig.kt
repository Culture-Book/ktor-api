package uk.co.culturebook.modules.culture.add_new.data

import io.ktor.server.config.*

object AddNewConfig {
    val ApplicationConfig.fileHost get() = property("ktor.file.host").getString()
    val ApplicationConfig.hostApiKey get() = property("ktor.file.apiKey").getString()
    val ApplicationConfig.hostToken get() = property("ktor.file.token").getString()
}