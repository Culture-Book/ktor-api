package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CultureRequest(
    val culture: Culture,
    val location: Location
)
