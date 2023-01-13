package uk.co.culturebook.utils

import org.junit.Test
import uk.co.culturebook.modules.cultural.add_new.location.data.Location
import kotlin.test.assertEquals

class DistanceTests {

    @Test
    fun testDistance() {
        val locationA = Location(3.0, 4.0)
        val locationB = Location(5.0, 6.0)
        val expected = 2.0
        val actual = distance(locationA, locationB)
        assertEquals(expected, actual)
    }

    @Test
    fun testDistanceNegative() {
        val locationA = Location(-3.0, -4.0)
        val locationB = Location(-5.0, -6.0)
        val expected = 2.0
        val actual = distance(locationA, locationB)
        assertEquals(expected, actual)
    }

    @Test
    fun testDistanceZero() {
        val locationA = Location(0.0, 0.0)
        val locationB = Location(0.0, 0.0)
        val expected = 0.0
        val actual = distance(locationA, locationB)
        assertEquals(expected, actual)
    }
}
