package uk.co.culturebook.modules.culture.add_new.data.models

import io.ktor.http.*
import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.culture.add_new.data.interfaces.external.MediaRoute
import java.net.URI

/**
 * A generic media file to be stored.
 *
 * @param fileName File's name, including the extension.
 * @param bucketName The bucket in which the file is saved to.
 * @param data The data of the File in a [ByteArray] form
 * */
@Serializable
data class MediaFile(
    val fileName: String,
    val bucketName: String,
    val data: ByteArray,
    val contentType: String = ContentType.Any.contentType
) {
    fun getUri(fileHost: String): URI =
        URI.create(fileHost + MediaRoute.Version.V1_STORAGE + MediaRoute.FileRoute.getRoute(bucketName, fileName))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (other !is MediaFile) return false

        if (fileName != other.fileName) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

const val BucketNameKey = "bucketName"