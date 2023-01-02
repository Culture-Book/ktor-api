package uk.co.culturebook.modules.authentication.routes.origin

import uk.co.culturebook.modules.authentication.routes.general.jwtPublicKey
import uk.co.culturebook.modules.authentication.routes.general.oauthPublicKey
import io.ktor.server.routing.*

fun Route.originAuthentication() {
    jwtPublicKey()
    oauthPublicKey()
    registration()
    signIn()
    registrationOrLogin()
}