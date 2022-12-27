package sig.g.modules.authentication.routes.origin

import io.ktor.server.routing.*
import sig.g.modules.authentication.routes.general.jwtPublicKey
import sig.g.modules.authentication.routes.general.oauthPublicKey

fun Routing.originAuthentication() {
    jwtPublicKey()
    oauthPublicKey()
    registration()
    signIn()
}