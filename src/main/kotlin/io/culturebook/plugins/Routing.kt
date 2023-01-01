package io.culturebook.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.modules.authentication.constants.COOKIE_NAME
import sig.g.modules.authentication.data.models.interfaces.AuthState

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(Sessions) {
        cookie<AuthState.Success>(COOKIE_NAME) {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}
