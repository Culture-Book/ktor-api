package uk.co.culturebook.modules.cultural.add_new.location.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Location(val latitude: Double, val longitude: Double)
