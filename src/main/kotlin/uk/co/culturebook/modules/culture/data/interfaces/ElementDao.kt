package uk.co.culturebook.modules.culture.data.interfaces

import uk.co.culturebook.modules.culture.data.models.BucketRequest
import uk.co.culturebook.modules.culture.data.models.Element
import uk.co.culturebook.modules.culture.data.models.MediaFile
import java.util.*

interface ElementDao {

    suspend fun createBucketForElement(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String,
    ): Boolean

    suspend fun deleteBucketForElement(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String,
    ): Boolean

    suspend fun getElement(id: UUID): Element?
    suspend fun getDuplicateElement(name: String, type: String): List<Element>
    suspend fun uploadMedia(apiKey: String, bearer: String, fileHost: String, files: List<MediaFile>): List<MediaFile>
    suspend fun linkElements(parentId: UUID, elementIds: List<UUID>): Boolean
    suspend fun insertElement(element: Element): Element?
    suspend fun deleteElement(elementId: UUID): Boolean
    suspend fun updateElement(element: Element): Boolean
}