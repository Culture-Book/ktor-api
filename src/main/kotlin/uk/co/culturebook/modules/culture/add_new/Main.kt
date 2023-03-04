package uk.co.culturebook.modules.culture.add_new

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.culture.add_new.routes.*

val client by lazy {
    HttpClient(CIO) {
        expectSuccess = true
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
}

fun Application.addNewModule() {
    routing {
        authenticate(AuthRoute.JwtAuth.route) {
            route(uk.co.culturebook.modules.culture.data.data.interfaces.AddNewRoute.AddNewVersion.V1.route) {
                addNewCulture()
                getCulture()

                submitElement()
                getElementRoutes()

                getContributionRoutes()
                uploadContributionRoute()
            }
        }
    }
}