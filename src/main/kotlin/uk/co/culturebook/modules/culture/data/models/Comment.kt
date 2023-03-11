package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class Comment(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val comment: String,
    val isMine: Boolean = false
)
