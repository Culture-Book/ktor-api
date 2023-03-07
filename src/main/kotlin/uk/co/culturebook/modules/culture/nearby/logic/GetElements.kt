package uk.co.culturebook.modules.culture.nearby.logic

import uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository
import uk.co.culturebook.modules.culture.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.culture.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.data.models.ElementType
import uk.co.culturebook.modules.culture.data.models.Location
import java.util.*

internal suspend fun getNearbyElements(
    userId: String,
    location: Location,
    types: List<ElementType>,
    radius: Double,
    page: Int
) =
    ElementRepository.getPreviewElements(userId, location, types, radius, page)

internal suspend fun getElements(
    userId: String,
    searchString: String,
    types: List<ElementType>,
    radius: Double,
    page: Int
) =
    ElementRepository.getPreviewElements(userId, searchString, types, radius, page)

internal suspend fun getContributions(
    userId: String,
    elementId: UUID,
    searchString: String,
    types: List<ElementType>,
    page: Int
) =
    ContributionRepository.getContributions(userId, elementId, searchString, types, page)

internal suspend fun getContributions(userId: String, searchString: String, types: List<ElementType>, page: Int) =
    ContributionRepository.getContributions(userId, searchString, types, page)

internal suspend fun getCultures(userId: String, cultureName: String) =
    CultureRepository.getCulturesByName(userId, cultureName)

internal suspend fun getCultures(userId: String, location: Location) =
    CultureRepository.getCulturesByLocation(userId, location)