package uk.co.culturebook.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun <T : Any?> T?.forceNotNull(call: ApplicationCall): T {
    if (this == null) call.respond(HttpStatusCode.BadRequest)
    return this!!
}