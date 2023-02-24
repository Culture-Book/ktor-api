package uk.co.culturebook.modules.culture.add_new.data.models

import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.culture.add_new.data.interfaces.external.MediaRoute
import java.net.URI

/**
 * A generic media file to be stored.
 *
 * @param fileName File's name, including the extension.
 * @param bucketName The bucket in which the file is saved to.
 * @param dataStream The data of the File in a [ByteReadChannel] form
 * */
@Serializable
data class MediaFile(
    val fileName: String,
    val bucketName: String,
    val dataStream: ByteReadChannel,
    val contentType: String = ContentType.Any.contentType
) {
    fun getUri(fileHost: String): URI =
        URI.create(fileHost + MediaRoute.Version.V1_STORAGE + MediaRoute.FileRoute.getRoute(bucketName, fileName))

    fun getParentUri(fileHost: String, parent: String): URI =
        URI.create(
            fileHost + MediaRoute.Version.V1_STORAGE + MediaRoute.FileRoute.getParentRoute(
                bucketName,
                parent,
                fileName
            )
        )

}

const val ElementKey = "element"
const val ContributionKey = "contribution"