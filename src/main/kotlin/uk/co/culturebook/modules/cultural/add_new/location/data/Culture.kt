package uk.co.culturebook.modules.cultural.add_new.location.data

import kotlinx.serialization.Serializable

@Serializable
data class Culture(val name: String, val location: Location)
