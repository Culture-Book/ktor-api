package uk.co.culturebook.utils

import uk.co.culturebook.modules.cultural.add_new.location.data.Location
import kotlin.math.pow
import kotlin.math.sqrt

fun distance(locationA: Location, locationB: Location) =
    sqrt((locationB.longitude - locationA.longitude).pow(2) + (locationB.latitude - locationB.latitude).pow(2))