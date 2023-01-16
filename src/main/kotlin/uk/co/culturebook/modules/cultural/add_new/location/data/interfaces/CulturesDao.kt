package uk.co.culturebook.modules.cultural.add_new.location.data.interfaces

import uk.co.culturebook.modules.cultural.add_new.location.data.models.Culture
import uk.co.culturebook.modules.cultural.add_new.location.data.models.Location
import java.util.*

interface CulturesDao {
    suspend fun getCulture(id: UUID): Culture?

    suspend fun getCulturesByName(name: String): List<Culture>

    suspend fun getCulturesByLocation(location: Location, kmLimit: Double = 1.0): List<Culture>

    suspend fun insertCulture(culture: Culture): Culture?

    suspend fun deleteCulture(id: UUID): Boolean

    suspend fun updateCulture(culture: Culture): Boolean
}