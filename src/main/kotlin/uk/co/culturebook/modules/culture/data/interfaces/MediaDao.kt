package uk.co.culturebook.modules.culture.data.interfaces

import uk.co.culturebook.modules.culture.data.models.Contribution
import uk.co.culturebook.modules.culture.data.models.Element
import uk.co.culturebook.modules.culture.data.models.Media
import java.util.*

interface MediaDao {
    suspend fun insertMedia(media: List<Media>): List<Media>
    suspend fun insertElementMedia(media: List<Media>, element: Element): Boolean
    suspend fun insertContributionMedia(media: List<Media>, contribution: Contribution): Boolean
    suspend fun getMediaByElement(elementId: UUID): List<Media>
    suspend fun getMediaByContribution(contributionId: UUID): List<Media>
}