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
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ContributionRepository
import uk.co.culturebook.modules.culture.add_new.data.interfaces.ContributionState
import uk.co.culturebook.modules.culture.add_new.data.models.Contribution
import uk.co.culturebook.modules.culture.add_new.data.models.ContributionKey
import uk.co.culturebook.modules.culture.add_new.data.models.MediaFile
import uk.co.culturebook.modules.culture.add_new.data.models.isValidElementTypeName
import uk.co.culturebook.modules.culture.add_new.logic.addContribution
import uk.co.culturebook.modules.culture.add_new.logic.getDuplicateContributions
import uk.co.culturebook.modules.culture.add_new.logic.uploadContributionMedia
import uk.co.culturebook.utils.forceNotNull
import java.util.*

internal fun Route.getContributionRoutes() {
    get(AddNewRoute.Contribution.Duplicate.route) {
        val name = call.request.queryParameters[AddNewRoute.Contribution.Duplicate.nameParam].forceNotNull(call)
        val type = call.request.queryParameters[AddNewRoute.Contribution.Duplicate.typeParam].forceNotNull(call)

        if (!type.isValidElementTypeName()) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val duplicates = getDuplicateContributions(name, type)

        if (duplicates.isEmpty()) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.Conflict, duplicates)
    }
}

internal fun Route.uploadContributionRoute() {
    val config = environment!!.config
    post(AddNewRoute.Contribution.Submit.route) {
        val multiPartData = call.receiveMultipart()
        val mediaFiles = arrayListOf<MediaFile>()
        val parts = multiPartData.readAllParts()

        val contributionPart = parts.find { part -> part is PartData.FormItem && part.name == ContributionKey }
        val contribution = if (contributionPart is PartData.FormItem) {
            Json.decodeFromString(Contribution.serializer(), contributionPart.value)
        } else null

        if (contribution == null) {
            call.respond(HttpStatusCode.BadRequest, ContributionState.Error.NoBucketName)
            return@post
        } else {
            val state = addContribution(
                apiKey = config.hostApiKey,
                bearer = config.hostToken,
                fileHost = config.fileHost,
                contribution = contribution
            )
            if (state is ContributionState.Error) {
                ContributionRepository.deleteContribution(contribution.id)
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
                    contribution.id.toString(),
                    it,
                    part.contentType?.contentType ?: ContentType.Any.contentType
                )
            }
        }

        val uploadFilesState = uploadContributionMedia(
            apiKey = config.hostApiKey,
            bearer = config.hostToken,
            fileHost = config.fileHost,
            mediaFiles = mediaFiles
        )

        if (uploadFilesState is ContributionState.Success.UploadSuccess) {
            call.respond(HttpStatusCode.OK, uploadFilesState.keys)
        } else {
            ContributionRepository.deleteContribution(contribution.id)
            call.respond(HttpStatusCode.BadRequest, uploadFilesState)
        }
    }
}