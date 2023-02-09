package uk.co.culturebook.modules.culture.add_new

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import uk.co.culturebook.modules.culture.add_new.data.data.interfaces.AddNewRoute
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
        route(AddNewRoute.AddNewVersion.V1.route) {
            addNewCulture()
            getCulture()

            submitElement()
            getElementRoutes()

            getContributionRoutes()
            uploadContributionRoute()
        }
    }
}