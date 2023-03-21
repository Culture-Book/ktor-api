package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class Contribution(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    val elementId: UUID,
    val name: String,
    val type: ElementType,
    val location: Location,
    val information: String,
    val eventType: EventType? = null,
    val linkElements: List<@Serializable(with = UUIDSerializer::class) UUID> = emptyList(),
    val media: List<Media> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val reactions: List<Reaction> = emptyList(),
    val favourite: Boolean = false,
    val isVerified: Boolean = false
)