package uk.co.culturebook.utils

import org.junit.Test
import uk.co.culturebook.modules.culture.data.models.Location
import kotlin.test.assertEquals

class DistanceTests {
    @Test
    fun testDistanceCalculation() {
        val location1 = Location(48.8588443, 2.2943506)
        val location2 = Location(40.7306458, -73.935242)
        val expectedDistance = 5827.0

        val actualDistance = getDistanceInKm(location1, location2)

        assertEquals(expectedDistance, actualDistance, 1.0)
    }

    @Test
    fun testSameLocation() {
        val location = Location(51.509865, -0.118092)
        val expectedDistance = 0.0

        val actualDistance = getDistanceInKm(location, location)

        assertEquals(expectedDistance, actualDistance)
    }
}
