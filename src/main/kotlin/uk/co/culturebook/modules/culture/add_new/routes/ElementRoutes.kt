package uk.co.culturebook.modules.culture.add_new.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.json.Json
import uk.co.culturebook.modules.culture.add_new.data.AddNewConfig.fileHost
import uk.co.culturebook.modules.culture.add_new.data.AddNewConfig.hostApiKey
import uk.co.culturebook.modules.culture.add_new.data.AddNewConfig.hostToken
import uk.co.culturebook.modules.culture.add_new.data.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ElementRepository.deleteElement
import uk.co.culturebook.modules.culture.add_new.data.interfaces.ElementState
import uk.co.culturebook.modules.culture.add_new.data.models.Element
import uk.co.culturebook.modules.culture.add_new.data.models.ElementKey
import uk.co.culturebook.modules.culture.add_new.data.models.MediaFile
import uk.co.culturebook.modules.culture.add_new.data.models.isValidElementTypeName
import uk.co.culturebook.modules.culture.add_new.logic.addElement
import uk.co.culturebook.modules.culture.add_new.logic.getDuplicateElements
import uk.co.culturebook.modules.culture.add_new.logic.uploadElementMedia
import uk.co.culturebook.utils.forceNotNull
import java.util.*

internal fun Route.getElementRoutes() {
    get(AddNewRoute.Element.Duplicate.route) {
        val name = call.request.queryParameters[AddNewRoute.Element.Duplicate.nameParam].forceNotNull(call)
        val type = call.request.queryParameters[AddNewRoute.Element.Duplicate.typeParam].forceNotNull(call)

        if (!type.isValidElementTypeName()) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val duplicates = getDuplicateElements(name, type)

        call.respond(HttpStatusCode.OK, duplicates)
    }
}

internal fun Route.submitElement() {
    val config = environment!!.config
    post(AddNewRoute.Element.Submit.route) {
        val multiPartData = call.receiveMultipart()
        val mediaFiles = arrayListOf<MediaFile>()
        val parts = multiPartData.readAllParts()

        val elementPart = parts.find { part -> part is PartData.FormItem && part.name == ElementKey }
        val element = if (elementPart is PartData.FormItem) {
            Json.decodeFromString(Element.serializer(), elementPart.value)
        } else null

        if (element == null) {
            call.respond(HttpStatusCode.BadRequest, ElementState.Error.NoBucketName)
            return@post
        } else {
            val state = addElement(
                apiKey = config.hostApiKey,
                bearer = config.hostToken,
                fileHost = config.fileHost,
                element = element
            )
            if (state is ElementState.Error) {
                deleteElement(element.id)
                call.respond(HttpStatusCode.BadRequest, state)
                return@post
            }
        }

        for (part in parts) {
            val stream = when (part) {
                is PartData.BinaryChannelItem -> part.provider()
                is PartData.BinaryItem -> part.provider().asStream().toByteReadChannel()
                is PartData.FileItem -> part.provider().asStream().toByteReadChannel()
                else -> null
            }

            stream?.let {
                mediaFiles += MediaFile(
                    UUID.randomUUID().toString(),
                    element.id.toString(),
                    it,
                    part.contentType?.contentType ?: ContentType.Any.contentType
                )
            }
        }

        if (mediaFiles.isNotEmpty()) {
            val uploadFilesState = uploadElementMedia(
                apiKey = config.hostApiKey,
                bearer = config.hostToken,
                fileHost = config.fileHost,
                mediaFiles = mediaFiles
            )
            if (uploadFilesState is ElementState.Success.UploadSuccess) {
                call.respond(HttpStatusCode.OK, uploadFilesState.keys)
            } else {
                deleteElement(element.id)
                call.respond(HttpStatusCode.BadRequest, uploadFilesState)
            }
        } else {
            call.respond(HttpStatusCode.OK, listOf(element.id.toString()))
        }
    }
}