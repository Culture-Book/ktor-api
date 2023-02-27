package uk.co.culturebook.modules.culture.data.interfaces

import uk.co.culturebook.modules.culture.data.models.BucketRequest
import uk.co.culturebook.modules.culture.data.models.Contribution
import uk.co.culturebook.modules.culture.data.models.ElementType
import uk.co.culturebook.modules.culture.data.models.MediaFile
import java.util.*

interface ContributionDao {

    suspend fun createBucketForContribution(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String,
    ): Boolean

    suspend fun getContribution(id: UUID): Contribution?
    suspend fun getDuplicateContribution(name: String, type: String): List<Contribution>
    suspend fun uploadMedia(
        parent: String,
        apiKey: String,
        bearer: String,
        fileHost: String,
        files: List<MediaFile>
    ): List<MediaFile>

    suspend fun deleteBucketForContribution(
        request: BucketRequest,
        apiKey: String,
        bearer: String,
        fileHost: String
    ): Boolean

    suspend fun linkContributions(parentId: UUID, elementIds: List<UUID>): Boolean
    suspend fun insertContribution(contribution: Contribution): Contribution?
    suspend fun deleteContribution(elementId: UUID): Boolean
    suspend fun updateContribution(element: Contribution): Boolean
    suspend fun getContributions(
        elementId: UUID,
        searchString: String,
        types: List<ElementType>,
        page: Int = 1,
        limit: Int = 3
    ): List<Contribution>
}