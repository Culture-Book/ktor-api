package uk.co.culturebook.modules.culture.add_new.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Location(val latitude: Double, val longitude: Double)
