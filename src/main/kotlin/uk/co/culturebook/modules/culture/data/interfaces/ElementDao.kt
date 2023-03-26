package uk.co.culturebook.modules.culture.data.interfaces

import uk.co.culturebook.modules.culture.data.models.*
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

    suspend fun getPreviewElements(
        userId: String,
        location: Location,
        types: List<ElementType> = emptyList(),
        kmLimit: Double = 1.0,
        page: Int = 1,
        limit: Int = 3
    ): List<Element>

    suspend fun getUserElements(
        userId: String,
        types: List<ElementType> = emptyList(),
        kmLimit: Double = 1.0,
        page: Int = 1,
        limit: Int = 3
    ): List<Element>

    suspend fun getFavouriteElements(
        userId: String,
        types: List<ElementType> = emptyList(),
        kmLimit: Double = 1.0,
        page: Int = 1,
        limit: Int = 3
    ): List<Element>


    suspend fun getPreviewElements(
        userId: String,
        searchString: String,
        types: List<ElementType> = emptyList(),
        kmLimit: Double = 1.0,
        page: Int = 0,
        limit: Int = 3
    ): List<Element>

    suspend fun getElement(id: UUID): Element?
    suspend fun getElement(userId: String, id: UUID): Element?
    suspend fun getDuplicateElement(name: String, type: String): List<Element>
    suspend fun uploadMedia(apiKey: String, bearer: String, fileHost: String, files: List<MediaFile>): List<MediaFile>
    suspend fun linkElements(parentId: UUID, elementIds: List<UUID>): Boolean
    suspend fun insertElement(element: Element, userId: String): Element?
    suspend fun deleteElement(elementId: UUID): Boolean
    suspend fun updateElement(element: Element): Boolean
}