package sig.g.modules.authentication.routes.google

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.httpClient
import sig.g.modules.authentication.data.UserRepository.exists
import sig.g.modules.authentication.data.models.GoogleUser
import sig.g.modules.authentication.data.models.UserSession
import sig.g.modules.authentication.data.models.states.AuthError
import sig.g.modules.authentication.data.models.states.AuthState
import sig.g.modules.authentication.routes.AuthRoute

internal fun Route.googleCallback() {
    get(AuthRoute.GoogleCallback.route) {
        val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
        call.sessions.set(UserSession(principal?.accessToken.toString(), principal?.refreshToken.toString()))
        val googleProfileUrl = AuthRoute.GoogleUser.route + principal?.accessToken.toString()
        val googleRequest = httpClient.get(googleProfileUrl) {
            contentType(ContentType.Any)
        }
        val googleUser = googleRequest.call.body<GoogleUser>()

        val response = if (googleUser.email.exists()) {
            httpClient.post("${AppConfig.AppHost.getProperty()}/${AuthRoute.Login.route}") {
                contentType(ContentType.Application.Json)
                setBody(googleUser.toUser())
            }
        } else {
            httpClient.post("${AppConfig.AppHost.getProperty()}/${AuthRoute.Register.route}") {
                contentType(ContentType.Application.Json)
                setBody(googleUser.toUser())
            }
        }

        when (response.status) {
            HttpStatusCode.Created -> call.respond(response.status, response.body<AuthState.AuthSuccess>())
            HttpStatusCode.BadRequest -> call.respond(response.status, response.body<AuthError>())
        }
    }
}