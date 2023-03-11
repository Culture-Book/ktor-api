package uk.co.culturebook.modules.culture.data.database.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import uk.co.culturebook.modules.culture.data.database.tables.contribution.ContributionMedia
import uk.co.culturebook.modules.culture.data.database.tables.contribution.Contributions
import uk.co.culturebook.modules.culture.data.database.tables.element.ElementMedia
import uk.co.culturebook.modules.culture.data.database.tables.element.Elements
import uk.co.culturebook.modules.culture.data.interfaces.MediaDao
import uk.co.culturebook.modules.culture.data.models.Contribution
import uk.co.culturebook.modules.culture.data.models.Element
import uk.co.culturebook.modules.culture.data.models.Media
import uk.co.culturebook.modules.database.dbQuery
import uk.co.culturebook.utils.toUri
import java.util.*
import uk.co.culturebook.modules.culture.data.database.tables.Media as MediaT

object MediaRepository : MediaDao {
    internal fun rowToMedia(resultRow: ResultRow) = Media(
        resultRow[MediaT.id],
        resultRow[MediaT.uri].toUri()!!,
        resultRow[MediaT.contentType]
    )

    override suspend fun insertMedia(media: List<Media>) = dbQuery {
        MediaT.batchInsert(media) {
            set(MediaT.id, it.id)
            set(MediaT.uri, it.uri.toString())
            set(MediaT.contentType, it.contentType)
        }.map(::rowToMedia)
    }

    override suspend fun insertElementMedia(media: List<Media>, element: Element) = dbQuery {
        ElementMedia.batchInsert(media) {
            set(ElementMedia.mediaId, it.id)
            set(ElementMedia.elementId, element.id)
        }.isNotEmpty()
    }

    override suspend fun insertContributionMedia(media: List<Media>, contribution: Contribution) = dbQuery {
        ContributionMedia.batchInsert(media) {
            set(ContributionMedia.mediaId, it.id)
            set(ContributionMedia.contributionId, contribution.id)
        }.isNotEmpty()
    }

    override suspend fun getMediaByElement(elementId: UUID): List<Media> = dbQuery {
        (MediaT innerJoin ElementMedia innerJoin Elements)
            .select { Elements.id eq elementId }
            .map(::rowToMedia)
    }

    override suspend fun getMediaByContribution(contributionId: UUID): List<Media> = dbQuery {
        (MediaT innerJoin ContributionMedia innerJoin Contributions)
            .select { Contributions.id eq contributionId }
            .map(::rowToMedia)
    }
}