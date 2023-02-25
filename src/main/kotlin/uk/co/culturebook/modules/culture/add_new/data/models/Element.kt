package uk.co.culturebook.modules.culture.add_new.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class Element(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    val cultureId: UUID,
    val name: String,
    val type: ElementType,
    val location: Location,
    val information: String,
    val eventType: EventType? = null,
    val linkElements: List<@Serializable(with = UUIDSerializer::class) UUID> = emptyList(),
    val media: List<Media> = emptyList()
)