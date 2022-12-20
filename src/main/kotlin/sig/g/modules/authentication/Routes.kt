package sig.g.modules.authentication

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

fun Routing.googleOauth() {
    authenticate("auth-oauth-google") {
        googleSignIn()
        onAuthentication()
    }
}

fun Routing.originAuth() {
    post("/register") {
        var user = call.receive<User>()
        val decryptedEmail = user.email.decrypt()

        if (decryptedEmail.isNullOrEmpty() || !decryptedEmail.isProperEmail()) {
            call.respond(HttpStatusCode.BadRequest, AuthError.InvalidEmail)
            return@post
        }

        user = user.copy(email = decryptedEmail)
        if (user.exists()) call.respond(HttpStatusCode.BadRequest)

        UserDAOFacadeImpl.registerUser(user)?.let {
            call.respond(HttpStatusCode.Created, it)
        } ?: call.respond(HttpStatusCode.BadRequest, AuthError.DatabaseError)
    }

    get(".well-known/public") {
        call.respond(AppConfig.JWTConfig.PublicKey.getProperty())
    }

    get(".well-known/jwt") {
//        val issuer = AppConfig.JWTConfig.Issuer.getProperty()
//        val audience = AppConfig.JWTConfig.Audience.getProperty()
//        val realm = AppConfig.JWTConfig.Realm.getProperty()
//
//        val jwkProvider = JwkProviderBuilder(issuer)
//            .cached(10, 24, TimeUnit.HOURS)
//            .rateLimited(10, 1, TimeUnit.MINUTES)
//            .build()
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