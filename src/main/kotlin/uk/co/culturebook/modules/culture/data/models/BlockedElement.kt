package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class BlockedElement(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID?
)
