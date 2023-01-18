package uk.co.culturebook.modules.culture.add_new.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class Element(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val type: IElementType,
    val location: Location,
    val information: String
)
