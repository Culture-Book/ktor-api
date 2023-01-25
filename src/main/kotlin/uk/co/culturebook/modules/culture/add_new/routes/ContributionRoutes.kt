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
import uk.co.culturebook.modules.culture.add_new.data.data.interfaces.AddNewRoute
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ContributionRepository
import uk.co.culturebook.modules.culture.add_new.data.interfaces.ContributionState
import uk.co.culturebook.modules.culture.add_new.data.models.BucketNameKey
import uk.co.culturebook.modules.culture.add_new.data.models.Contribution
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

internal fun Route.addContributionRoutes() {
    val config = environment!!.config
    post(AddNewRoute.Contribution.Submit.route) {
        val callContribution = call.receive<Contribution>()
        val contributionState = addContribution(
            apiKey = config.hostApiKey,
            bearer = config.hostToken,
            fileHost = config.fileHost,
            contribution = callContribution
        )
        if (contributionState is ContributionState.Success.AddContribution) {
            call.respond(HttpStatusCode.OK, contributionState.contribution)
        } else {
            ContributionRepository.deleteContribution(callContribution.id)
            call.respond(HttpStatusCode.BadRequest, contributionState)
        }
    }
}

internal fun Route.uploadContributionMediaRoute() {
    val config = environment!!.config
    post(AddNewRoute.Contribution.Submit.Upload.route) {
        val multiPartData = call.receiveMultipart()
        val mediaFiles = arrayListOf<MediaFile>()
        var bucketName: String? = null
        val parts = multiPartData.readAllParts()

        parts.forEach { part ->
            if (part is PartData.FormItem && part.name == BucketNameKey) bucketName = part.value
        }

        if (bucketName == null) {
            call.respond(HttpStatusCode.BadRequest, ContributionState.Error.NoBucketName)
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

        val uploadFilesState = uploadContributionMedia(
            apiKey = config.hostApiKey,
            bearer = config.hostToken,
            fileHost = config.fileHost,
            mediaFiles = mediaFiles
        )

        if (uploadFilesState is ContributionState.Success.UploadSuccess) {
            call.respond(HttpStatusCode.OK, uploadFilesState.keys)
        } else {
            call.respond(HttpStatusCode.BadRequest, uploadFilesState)
        }
    }

}