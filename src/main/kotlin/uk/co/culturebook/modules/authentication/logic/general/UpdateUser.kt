package uk.co.culturebook.modules.authentication.logic.general

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.jvm.javaio.*
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.interfaces.AuthRoute
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.data.models.VerificationStatusRequest
import uk.co.culturebook.modules.authentication.logic.authenticated.getUserId
import uk.co.culturebook.modules.culture.data.AddNewConfig.fileHost
import uk.co.culturebook.modules.culture.data.AddNewConfig.hostApiKey
import uk.co.culturebook.modules.culture.data.AddNewConfig.hostToken
import uk.co.culturebook.modules.culture.data.models.BucketRequest
import uk.co.culturebook.modules.culture.data.models.MediaFile
import uk.co.culturebook.utils.forceNotNull

internal fun Route.removeProfileUri() {
    delete(AuthRoute.User.ProfilePicture.route) {
        val deleted = UserRepository.removeProfileUri(getUserId())
        if (deleted) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

internal fun Route.uploadProfileImage(config: ApplicationConfig) {
    post(AuthRoute.User.ProfilePicture.route) { _ ->
        val multiPartData = call.receiveMultipart()
        val part = multiPartData.readPart()

        val stream =
            when (part) {
                is PartData.BinaryChannelItem -> part.provider()
                is PartData.BinaryItem -> part.provider().asStream().toByteReadChannel()
                is PartData.FileItem -> part.provider().asStream().toByteReadChannel()
                else -> null
            }

        val mediaFile = stream?.let {
            MediaFile(
                "profile-image",
                getUserId(),
                it,
                "${part?.contentType?.contentType ?: "*"}/${part?.contentType?.contentSubtype ?: "*"}"
            )
        }

        val uploadedImage = mediaFile?.let {
            UserRepository.uploadProfileImage(
                getUserId(),
                config.hostApiKey,
                config.hostToken,
                config.fileHost,
                it
            )
        }.forceNotNull(call)

        val updated = UserRepository.updateProfileUri(getUserId(), uploadedImage.getUri(config.fileHost))

        if (updated) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

internal fun Route.updateDisplayNameAndEmail() {
    put(AuthRoute.User.route) {
        val user = call.receive<User>()
        val updated = UserRepository.updateDisplayNameAndEmail(getUserId(), user)
        if (updated) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

internal fun Route.requestVerificationStatus() {
    post(AuthRoute.User.VerificationStatus.route) {
        val request = call.receive<VerificationStatusRequest>()
        val status =
            UserRepository.requestVerificationStatus(getUserId(), request.reason, call.application.environment.config)
        call.respond(status)
    }
}

internal fun Route.deleteUser() {
    delete(AuthRoute.User.route) {
        val config = call.application.environment.config
        val userId = getUserId()
        val deletedUserBucket = UserRepository.deleteBucketForUser(
            BucketRequest(userId, userId),
            config.hostApiKey,
            config.hostToken,
            config.fileHost
        )
        if (!deletedUserBucket) {
            call.respond(HttpStatusCode.InternalServerError)
            return@delete
        }
        val deleted = UserRepository.deleteUser(userId)

        if (deleted) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}