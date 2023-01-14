package uk.co.culturebook.modules.cultural.add_new.location.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Element(
    val name: String,
    val type: ElementType,
    val location: Location,
    val information: String,
    val linkedElements: List<Element>,
    val media: List<String>
)
