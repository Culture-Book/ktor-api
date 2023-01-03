package uk.co.culturebook.modules.authentication.routes.origin

import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.routes.general.forgotPasswordRoute
import uk.co.culturebook.modules.authentication.routes.general.jwtPublicKey
import uk.co.culturebook.modules.authentication.routes.general.oauthPublicKey
import uk.co.culturebook.modules.authentication.routes.general.resetPassword

fun Route.originAuthentication() {
    jwtPublicKey()
    oauthPublicKey()
    registration()
    signIn()
    registrationOrLogin()
    forgotPasswordRoute()
    resetPassword()
}