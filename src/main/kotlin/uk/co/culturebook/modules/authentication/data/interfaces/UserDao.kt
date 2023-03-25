package uk.co.culturebook.modules.authentication.data.interfaces

import io.ktor.server.config.*
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.culture.data.models.BucketRequest
import uk.co.culturebook.modules.culture.data.models.MediaFile
import java.net.URI

interface UserDao {
    suspend fun String.exists(): Boolean
    suspend fun getUser(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun registerUser(user: User): User?
    suspend fun updateTos(userId: String): Boolean
    suspend fun updatePrivacy(userId: String): Boolean
    suspend fun updatePassword(userId: String, password: String): Boolean
    suspend fun updatePassword(
        userId: String,
        password: String,
        newPassword: String,
        config: ApplicationConfig
    ): Boolean

    suspend fun updateProfileUri(userId: String, profileURI: URI): Boolean
    suspend fun uploadProfileImage(
        userId: String,
        apiKey: String,
        bearer: String,
        fileHost: String,
        profileImage: MediaFile
    ): MediaFile?

    suspend fun createBucketForUser(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String
    ): Boolean

    suspend fun deleteBucketForUser(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String
    ): Boolean

    suspend fun removeProfileUri(userId: String): Boolean
    suspend fun updateDisplayNameAndEmail(userId: String, user: User): Boolean
    suspend fun requestVerificationStatus(userId: String, reason: String, config: ApplicationConfig): Boolean
    suspend fun deleteUser(userId: String): Boolean
}