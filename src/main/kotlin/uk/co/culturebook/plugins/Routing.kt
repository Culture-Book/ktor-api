package uk.co.culturebook.plugins

import uk.co.culturebook.modules.authentication.constants.COOKIE_NAME
import uk.co.culturebook.modules.authentication.data.models.interfaces.AuthState
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
