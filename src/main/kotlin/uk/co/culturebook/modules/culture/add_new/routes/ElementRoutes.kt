package uk.co.culturebook.modules.culture.add_new.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import uk.co.culturebook.modules.culture.add_new.data.AddNewConfig.fileHost
import uk.co.culturebook.modules.culture.add_new.data.AddNewConfig.hostApiKey
import uk.co.culturebook.modules.culture.add_new.data.AddNewConfig.hostToken
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.add_new.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.add_new.data.interfaces.ElementState
import uk.co.culturebook.modules.culture.add_new.data.models.BucketNameKey
import uk.co.culturebook.modules.culture.add_new.data.models.Element
import uk.co.culturebook.modules.culture.add_new.data.models.MediaFile
import uk.co.culturebook.modules.culture.add_new.data.models.isValidElementTypeName
import uk.co.culturebook.modules.culture.add_new.logic.addElement
import uk.co.culturebook.modules.culture.add_new.logic.getDuplicateElements
import uk.co.culturebook.modules.culture.add_new.logic.uploadMedia
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

        if (duplicates.isEmpty()) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.Conflict, duplicates)
    }
}

internal fun Route.addElementRoutes() {
    val config = environment!!.config
    post(AddNewRoute.Element.route) {
        val callElement = call.receive<Element>()
        val elementState = addElement(
            apiKey = config.hostApiKey,
            bearer = config.hostToken,
            fileHost = config.fileHost,
            element = callElement
        )
        if (elementState is ElementState.Success.AddElement) {
            call.respond(HttpStatusCode.OK, elementState.element)
        } else {
            ElementRepository.deleteElement(callElement.id)
            call.respond(HttpStatusCode.BadRequest, elementState)
        }
    }
}

internal fun Route.uploadMediaRoute() {
    val config = environment!!.config
    post(AddNewRoute.Element.Submit.Upload.route) {
        val multiPartData = call.receiveMultipart()
        val mediaFiles = arrayListOf<MediaFile>()
        var bucketName: String? = null
        val parts = multiPartData.readAllParts()

        parts.forEach { part ->
            if (part is PartData.FormItem && part.name == BucketNameKey) bucketName = part.value
        }

        if (bucketName == null) {
            call.respond(HttpStatusCode.BadRequest, ElementState.Error.NoBucketName)
            return@post
        }

        parts.forEach { part ->
            var bytes: ByteArray? = null

            when (part) {
                is PartData.BinaryChannelItem -> part.provider().readAvailable { bytes = it.moveToByteArray() }
                is PartData.BinaryItem -> bytes = part.provider().readBytes()
                is PartData.FileItem -> bytes = part.provider().readBytes()
                else -> {}
            }
            bytes?.let {
                mediaFiles += MediaFile(
                    UUID.randomUUID().toString(),
                    bucketName!!,
                    it,
                    part.contentType?.contentType ?: ContentType.Any.contentType
                )
            }
        }

        val uploadFilesState = uploadMedia(
            apiKey = config.hostApiKey,
            bearer = config.hostToken,
            fileHost = config.fileHost,
            mediaFiles = mediaFiles
        )

        if (uploadFilesState is ElementState.Success.UploadSuccess) {
            call.respond(HttpStatusCode.OK, uploadFilesState.keys)
        } else {
            call.respond(HttpStatusCode.BadRequest, uploadFilesState)
        }
    }

}