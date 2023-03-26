package uk.co.culturebook.data.models.cultural

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.culture.data.models.BlockedContribution
import uk.co.culturebook.modules.culture.data.models.BlockedCulture
import uk.co.culturebook.modules.culture.data.models.BlockedElement

@Serializable
data class BlockedList(
    val blockedElement: List<BlockedElement> = emptyList(),
    val blockedContribution: List<BlockedContribution> = emptyList(),
    val blockedCulture: List<BlockedCulture> = emptyList()
)
