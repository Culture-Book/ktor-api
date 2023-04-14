package uk.co.culturebook.modules.http

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ClientFactory {
    companion object {
        private lateinit var instance: HttpClient
        fun <T : HttpClientEngineConfig> getInstance(engine: HttpClientEngineFactory<T>): HttpClient {
            if (!::instance.isInitialized) {
                instance = HttpClient(engine) {
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
            return instance
        }
    }
}