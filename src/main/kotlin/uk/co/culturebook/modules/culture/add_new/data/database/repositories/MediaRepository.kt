package uk.co.culturebook.modules.culture.add_new.data.database.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import uk.co.culturebook.modules.culture.add_new.data.database.tables.contribution.ContributionMedia
import uk.co.culturebook.modules.culture.add_new.data.database.tables.element.ElementMedia
import uk.co.culturebook.modules.culture.add_new.data.models.Contribution
import uk.co.culturebook.modules.culture.add_new.data.models.Element
import uk.co.culturebook.modules.culture.add_new.data.models.Media
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.utils.toUri
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Media as MediaT

object MediaRepository {
    private fun rowToMedia(resultRow: ResultRow) = Media(
        resultRow[MediaT.id],
        resultRow[MediaT.uri].toUri()!!)


    suspend fun insertMedia(media: List<Media>) = dbQuery {
        MediaT.batchInsert(media) {
            set(MediaT.id, it.id)
            set(MediaT.uri, it.uri.toString())
        }.map(::rowToMedia)
    }

    suspend fun insertElementMedia(media: List<Media>, element: Element) = dbQuery {
        ElementMedia.batchInsert(media) {
            set(ElementMedia.mediaId, it.id)
            set(ElementMedia.elementId, element.id)
        }.isNotEmpty()
    }
    suspend fun insertContributionMedia(media: List<Media>, contribution: Contribution) = dbQuery {
        ContributionMedia.batchInsert(media) {
            set(ContributionMedia.mediaId, it.id)
            set(ContributionMedia.contributionId, contribution.id)
        }.isNotEmpty()
    }

}