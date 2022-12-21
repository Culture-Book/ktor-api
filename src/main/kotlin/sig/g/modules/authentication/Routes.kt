package sig.g.modules.authentication

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.modules.authentication.data.User
import sig.g.modules.authentication.data.UserDAOFacadeImpl
import sig.g.modules.authentication.data.UserDAOFacadeImpl.exists
import sig.g.modules.authentication.data.UserSession
import sig.g.modules.authentication.data.normalize
import java.util.concurrent.TimeUnit

fun Routing.googleOauth() {
    authenticate("auth-oauth-google") {
        googleSignIn()
        onAuthentication()
    }
}

fun Routing.originAuth() {
    post("/register") {
        var user = call.receive<User>()
        val decryptedEmail = user.email.decodeOAuth()

        if (decryptedEmail.isNullOrEmpty() || !decryptedEmail.isProperEmail()) {
            call.respond(HttpStatusCode.BadRequest, AuthError.InvalidEmail)
            return@post
        }

        user = user.copy(email = decryptedEmail)
        user = user.normalize()

        if (user.exists()) {
            call.respond(HttpStatusCode.BadRequest, AuthError.DuplicateEmail)
            return@post
        }



        UserDAOFacadeImpl.registerUser(user)?.let {
            call.sessions.set(UserSession(accessToken = generateJwt(it.userId)!!))
            call.respond(HttpStatusCode.Created)
        } ?: call.respond(HttpStatusCode.BadRequest, AuthError.DatabaseError)
    }

    get(".well-known/jwt/public") {
        call.respond(AppConfig.JWTConfig.PublicKey.getProperty())
    }

    get(".well-known/oauth/public") {
        call.respond(AppConfig.OAuthConfig.PublicKey.getProperty())
    }

    get(".well-known/jwt") {
        call.respond(AppConfig.JWTConfig.PublicKey.getProperty())
    }
}

private fun Route.googleSignIn() {
    get("glogin") {
        call.respondRedirect("/register")
    }
}

private fun Route.onAuthentication(redirectUrl: String = "/") {
    get("/callback") {
        val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
        call.sessions.set(UserSession(principal?.accessToken.toString(), principal?.refreshToken.toString()))
        call.respondRedirect(redirectUrl)
    }
}