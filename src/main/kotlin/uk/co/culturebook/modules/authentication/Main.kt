package uk.co.culturebook.modules.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.logic.general.*
import uk.co.culturebook.modules.authentication.routes.authenticated.getUserDetailsRoute
import uk.co.culturebook.modules.authentication.routes.authenticated.updateTos
import uk.co.culturebook.modules.authentication.routes.general.*
import uk.co.culturebook.modules.authentication.routes.resources.getAssetLinks
import uk.co.culturebook.modules.authentication.routes.resources.getPrivacy
import uk.co.culturebook.modules.authentication.routes.resources.getTos

fun Application.authenticationModule() {
    val config = environment.config

    authentication {
        configureJwt(config)
    }

    routing {
        // Auth V1
        route(AuthRoute.AuthRouteVersion.V1.route) {
            publicKey(config)
            registration(config)
            signIn(config)
            registrationOrLogin(config)
            forgotPasswordRoute(config)
            resetPassword(config)
            refreshJwt(config)

            authenticate(AuthRoute.JwtAuth.route) {
                removeProfileUri()
                updateDisplayNameAndEmail()
                requestVerificationStatus()
                uploadProfileImage(config)
                updatePasswordRoute(config)
                getUserDetailsRoute(config)
                deleteUser()
                updateTos()
            }
        }
    }

    // Resources
    routing {
        getTos()
        getPrivacy()
        getAssetLinks()
    }
}