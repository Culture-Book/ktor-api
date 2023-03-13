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
import uk.co.culturebook.modules.culture.add_new.logic.addContribution
import uk.co.culturebook.modules.culture.add_new.logic.getDuplicateContributions
import uk.co.culturebook.modules.culture.add_new.logic.uploadContributionMedia
import uk.co.culturebook.modules.culture.data.AddNewConfig.fileHost
import uk.co.culturebook.modules.culture.data.AddNewConfig.hostApiKey
import uk.co.culturebook.modules.culture.data.AddNewConfig.hostToken
import uk.co.culturebook.modules.culture.data.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository
import uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository.deleteBucketForContribution
import uk.co.culturebook.modules.culture.data.database.repositories.MediaRepository
import uk.co.culturebook.modules.culture.data.interfaces.ContributionState
import uk.co.culturebook.modules.culture.data.models.*
import uk.co.culturebook.utils.forceNotNull
import uk.co.culturebook.utils.toUri
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

        call.respond(HttpStatusCode.OK, duplicates)
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
                    "${part.contentType?.contentType ?: "*"}/${part.contentType?.contentSubtype ?: "*"}"
                )
            }
        }

        if (mediaFiles.isNotEmpty()) {
            val uploadFilesState = uploadContributionMedia(
                parentElement = contribution.elementId.toString(),
                apiKey = config.hostApiKey,
                bearer = config.hostToken,
                fileHost = config.fileHost,
                mediaFiles = mediaFiles
            )

            if (uploadFilesState is ContributionState.Success.UploadSuccess) {
                val media = uploadFilesState.media.map { Media(uri = it.first.toUri()!!, contentType = it.second) }
                val addedMedia = MediaRepository.insertMedia(media)
                val contributionMediaAdded = MediaRepository.insertContributionMedia(addedMedia, contribution)
                if (contributionMediaAdded) {
                    val updatedContribution = contribution.copy(media = addedMedia)
                    call.respond(HttpStatusCode.OK, updatedContribution)
                } else {
                    ContributionRepository.deleteContribution(contribution.id)
                    deleteBucketForContribution(
                        request = BucketRequest(contribution.id.toString(), contribution.id.toString()),
                        apiKey = config.hostApiKey,
                        bearer = config.hostToken,
                        fileHost = config.fileHost
                    )
                    call.respond(HttpStatusCode.BadRequest, uploadFilesState)
                }
            } else {
                ContributionRepository.deleteContribution(contribution.id)
                call.respond(HttpStatusCode.BadRequest, uploadFilesState)
            }
        } else {
            call.respond(HttpStatusCode.OK, contribution)
        }
    }
}