package uk.co.culturebook.modules.routing

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.sessions.*
import uk.co.culturebook.modules.authentication.data.constants.COOKIE_NAME
import uk.co.culturebook.modules.authentication.data.interfaces.AuthState
import kotlin.collections.set

fun Application.routingModule() {
    install(AutoHeadResponse)
    install(Sessions) {
        cookie<AuthState.Success>(COOKIE_NAME) {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}
