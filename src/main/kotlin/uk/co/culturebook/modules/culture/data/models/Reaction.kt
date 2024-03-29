package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class Reaction(
    @Serializable(with = UUIDSerializer::class)
    @Transient
    val id: UUID? = null,
    val reaction: String,
    val isMine: Boolean = false
)
