package uk.co.culturebook.base

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.server.config.*
import uk.co.culturebook.modules.culture.data.AddNewConfig.fileHost
import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute

class TestHttpEngine(private val testConfig: ApplicationConfig) : HttpClientEngineFactory<TestHttpEngine.Config> {
    class Config : HttpClientEngineConfig()

    private val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

    override fun create(block: Config.() -> Unit): HttpClientEngine {
        return MockEngine.create {
            addHandler { request ->
                when {
                    request.url.fullPath.contains(MediaRoute.BucketRoute.getBucket(testConfig.fileHost)) ->
                        respond("", HttpStatusCode.OK, responseHeaders)

                    else -> error("Unhandled ${request.url}")
                }
            }
        }
    }
}