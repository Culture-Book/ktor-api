package io.culturebook.plugins

import io.culturebook.modules.authentication.constants.COOKIE_NAME
import io.culturebook.modules.authentication.data.models.interfaces.AuthState
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.sessions.*
import kotlin.collections.set

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(Sessions) {
        cookie<AuthState.Success>(COOKIE_NAME) {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}
