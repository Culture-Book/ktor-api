package uk.co.culturebook.utils

import uk.co.culturebook.modules.culture.add_new.location.data.models.Location
import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun getDistanceInKm(location1: Location, location2: Location): Double {
    val earthRadius = 6371.0 // Earth's radius in kilometers
    val latDistance = toRadians(location2.latitude - location1.latitude)
    val lonDistance = toRadians(location2.longitude - location1.longitude)
    val a =
        sin(latDistance / 2) * sin(latDistance / 2) + cos(toRadians(location1.latitude)) * cos(toRadians(location2.latitude)) * sin(
            lonDistance / 2
        ) * sin(lonDistance / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

fun getDistanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Earth's radius in kilometers
    val latDistance = toRadians(lat2 - lat1)
    val lonDistance = toRadians(lon2 - lon1)
    val a =
        sin(latDistance / 2) * sin(latDistance / 2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(
            lonDistance / 2
        ) * sin(lonDistance / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}