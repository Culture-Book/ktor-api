package uk.co.culturebook.modules.culture.add_new.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.URISerializer
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.net.URI
import java.util.*

@Serializable
data class Element(
    val name: String,
    val type: ElementType,
    val location: Location,
    val information: String,
    val linkedElements: List<@Serializable(with = UUIDSerializer::class) UUID>,
    val media: List<@Serializable(with = URISerializer::class) URI>
)
