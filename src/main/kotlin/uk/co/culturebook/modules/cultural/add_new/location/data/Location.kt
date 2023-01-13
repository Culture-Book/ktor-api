package uk.co.culturebook.modules.cultural.add_new.location.data

import kotlinx.serialization.Serializable

@Serializable
data class Location(val longitude: Double, val latitude: Double)
