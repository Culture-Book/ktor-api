package uk.co.culturebook.modules.culture.data.database.tables.contribution

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import uk.co.culturebook.modules.culture.data.database.tables.Media

object ContributionMedia : Table() {
    val contributionId = uuid("contribution_id").references(
        Contributions.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val mediaId =
        uuid("media_id").references(Media.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}