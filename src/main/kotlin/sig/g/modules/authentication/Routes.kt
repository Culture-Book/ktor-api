package sig.g.modules.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Routing.googleOauth() {
    authenticate("auth-oauth-google") {
        googleSignIn()
        onAuthentication()
    }
}

private fun Route.googleSignIn() {
    get("glogin") {
        call.respondRedirect("/callback")
    }
}

private fun Route.onAuthentication(redirectUrl: String = "/") {
    get("/callback") {
        val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
        call.sessions.set(UserSession(principal?.accessToken.toString(), principal?.refreshToken.toString()))
        call.respondRedirect(redirectUrl)
    }
}