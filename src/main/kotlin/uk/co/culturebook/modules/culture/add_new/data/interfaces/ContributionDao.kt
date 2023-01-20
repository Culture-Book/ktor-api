package uk.co.culturebook.modules.culture.add_new.data.interfaces

import uk.co.culturebook.modules.culture.add_new.data.models.BucketRequest
import uk.co.culturebook.modules.culture.add_new.data.models.Contribution
import uk.co.culturebook.modules.culture.add_new.data.models.MediaFile
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
    suspend fun uploadMedia(apiKey: String, bearer: String, fileHost: String, files: List<MediaFile>): List<MediaFile>
    suspend fun linkContributions(parentId: UUID, elementIds: List<UUID>): Boolean
    suspend fun insertContribution(element: Contribution): Contribution?
    suspend fun deleteContribution(elementId: UUID): Boolean
    suspend fun updateContribution(element: Contribution): Boolean
}