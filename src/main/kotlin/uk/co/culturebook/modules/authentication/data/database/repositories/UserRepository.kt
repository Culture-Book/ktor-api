package uk.co.culturebook.modules.authentication.data.database.repositories

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.Constants
import uk.co.culturebook.modules.authentication.data.AuthConfig.emailAccount
import uk.co.culturebook.modules.authentication.data.AuthConfig.emailHost
import uk.co.culturebook.modules.authentication.data.AuthConfig.emailPassword
import uk.co.culturebook.modules.authentication.data.AuthConfig.smtpPort
import uk.co.culturebook.modules.authentication.data.database.tables.Users
import uk.co.culturebook.modules.authentication.data.enums.VerificationStatus
import uk.co.culturebook.modules.authentication.data.interfaces.UserDao
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.decodeOAuth
import uk.co.culturebook.modules.authentication.logic.general.sendEmail
import uk.co.culturebook.modules.culture.add_new.client
import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute
import uk.co.culturebook.modules.culture.data.models.BucketRequest
import uk.co.culturebook.modules.culture.data.models.MediaFile
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.utils.toUri
import java.net.URI
import java.time.LocalDateTime

object UserRepository : UserDao {

    private fun resultRowToUser(row: ResultRow) = User(
        userId = row[Users.userId],
        profileUri = row[Users.profileUri].toUri(),
        displayName = row[Users.displayName],
        password = row[Users.password],
        email = row[Users.email],
        tosAccept = row[Users.tosAccept],
        privacyAccept = row[Users.privacyAccept],
        verificationStatus = row[Users.verificationStatus],
        registrationStatus = row[Users.registrationStatus],
    )

    override suspend fun String.exists(): Boolean = dbQuery {
        Users.select(Users.email eq this).singleOrNull() != null
    }

    override suspend fun getUser(userId: String): User? = dbQuery {
        Users.select(Users.userId eq userId)
            .map(UserRepository::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select(Users.email eq email)
            .map(UserRepository::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun registerUser(user: User): User? = dbQuery {
        val insertStatement = Users.insert {
            it[userId] = user.userId
            it[profileUri] = user.profileUri.toString()
            it[displayName] = user.displayName
            it[password] = user.password
            it[email] = user.email
            it[tosAccept] = LocalDateTime.now()
            it[privacyAccept] = LocalDateTime.now()
            it[verificationStatus] = user.verificationStatus
            it[registrationStatus] = user.registrationStatus
        }
        insertStatement.resultedValues?.singleOrNull()?.let(UserRepository::resultRowToUser)
    }

    override suspend fun updateTos(userId: String): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[tosAccept] = LocalDateTime.now()
        } > 0
    }

    override suspend fun updatePrivacy(userId: String): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[privacyAccept] = LocalDateTime.now()
        } > 0
    }

    override suspend fun updatePassword(userId: String, password: String): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[Users.password] = password
        } > 0
    }

    override suspend fun updatePassword(
        userId: String,
        password: String,
        newPassword: String,
        config: ApplicationConfig
    ): Boolean = dbQuery {
        val oldPassword = Users.select(Users.userId eq userId)
            .map(UserRepository::resultRowToUser)
            .singleOrNull()
            ?.password
            ?.decodeOAuth(config)

        if (oldPassword != password) return@dbQuery false
        Users.update({ Users.userId eq userId }) {
            it[Users.password] = newPassword
        } > 0
    }

    override suspend fun updateProfileUri(userId: String, profileURI: URI): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[profileUri] = profileURI.toString()
        } > 0
    }

    override suspend fun createBucketForUser(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String
    ): Boolean {
        val response = client.post(MediaRoute.BucketRoute.getBucket(fileHost)) {
            headers {
                append(Constants.Headers.Authorization, "Bearer $bearer")
                append(Constants.Headers.ApiKey, apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status == HttpStatusCode.OK
    }

    override suspend fun deleteBucketForUser(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String
    ): Boolean {
        val emptyResponse = client.post(MediaRoute.BucketRoute.emptyBucket(fileHost, request.id)) {
            headers {
                append(Constants.Headers.Authorization, "Bearer $bearer")
                append(Constants.Headers.ApiKey, apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.status == HttpStatusCode.OK
        if (!emptyResponse) return false
        val response = client.delete(MediaRoute.BucketRoute.getBucket(fileHost, request.id)) {
            headers {
                append(Constants.Headers.Authorization, "Bearer $bearer")
                append(Constants.Headers.ApiKey, apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status == HttpStatusCode.OK
    }

    override suspend fun uploadProfileImage(
        userId: String,
        apiKey: String,
        bearer: String,
        fileHost: String,
        profileImage: MediaFile
    ): MediaFile? {
        // what happens if it doesn't exit?
        try {
            client.delete(profileImage.getUri(fileHost).toURL()) {
                headers {
                    append(Constants.Headers.Authorization, "Bearer $bearer")
                    append(Constants.Headers.ApiKey, apiKey)
                }
                contentType(ContentType.parse(profileImage.contentType))
                setBody(profileImage.dataStream)
            }
        } catch (e: Exception) {
            println(e)
        }

        val response = client.post(profileImage.getUri(fileHost).toURL()) {
            headers {
                append(Constants.Headers.Authorization, "Bearer $bearer")
                append(Constants.Headers.ApiKey, apiKey)
            }
            contentType(ContentType.parse(profileImage.contentType))
            setBody(profileImage.dataStream)
        }
        return if (response.status == HttpStatusCode.OK) {
            profileImage
        } else {
            null
        }
    }

    override suspend fun removeProfileUri(userId: String): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[profileUri] = ""
        } > 0
    }

    override suspend fun updateDisplayNameAndEmail(userId: String, user: User): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[displayName] = user.displayName
            it[email] = user.email
        } > 0
    }

    override suspend fun requestVerificationStatus(userId: String, reason: String, config: ApplicationConfig): Boolean =
        dbQuery {
            sendEmail(
                "Verification Request for $userId",
                "A verification request has been made for user with id $userId. Reason: $reason",
                email = config.emailAccount,
                host = config.emailHost,
                smtp = config.smtpPort.toInt(),
                account = config.emailAccount,
                password = config.emailPassword
            )
            Users.update({ Users.userId eq userId }) {
                it[verificationStatus] = VerificationStatus.Pending.ordinal
            } > 0
        }

    override suspend fun deleteUser(userId: String): Boolean = dbQuery {
        Users.deleteWhere { Users.userId eq userId } > 0
    }
}