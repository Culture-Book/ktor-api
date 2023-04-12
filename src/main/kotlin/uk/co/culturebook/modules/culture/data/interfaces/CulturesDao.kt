package uk.co.culturebook.modules.culture.data.interfaces

import uk.co.culturebook.modules.culture.data.models.Culture
import uk.co.culturebook.modules.culture.data.models.Location
import java.util.*

interface CulturesDao {
    suspend fun getCulture(id: UUID): Culture?

    suspend fun getCulturesByName(userId: String, name: String): List<Culture>

    suspend fun getCulturesByLocation(userId: String, location: Location, kmLimit: Double = 1.0): List<Culture>

    suspend fun insertCulture(culture: Culture, userId: String): Culture?

    suspend fun deleteCulture(id: UUID): Boolean

    suspend fun updateCulture(culture: Culture): Boolean

    suspend fun getUserCultures(userId: String, page: Int = 1, limit: Int = 3): List<Culture>

    suspend fun getFavouriteCultures(userId: String): List<Culture>
}