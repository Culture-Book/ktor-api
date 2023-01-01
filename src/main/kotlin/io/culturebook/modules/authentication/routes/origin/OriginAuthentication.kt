package io.culturebook.modules.authentication.routes.origin

import io.culturebook.modules.authentication.routes.general.jwtPublicKey
import io.culturebook.modules.authentication.routes.general.oauthPublicKey
import io.ktor.server.routing.*

fun Route.originAuthentication() {
    jwtPublicKey()
    oauthPublicKey()
    registration()
    signIn()
    registrationOrLogin()
}