package uk.co.culturebook.modules.culture.nearby.logic

import uk.co.culturebook.modules.culture.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.data.models.ElementType
import uk.co.culturebook.modules.culture.data.models.Location

internal suspend fun getNearbyElements(location: Location, types: List<ElementType>, radius: Double, page: Int) =
    ElementRepository.getPreviewElements(location, types, radius, page)

internal suspend fun getElements(searchString: String, types: List<ElementType>, radius: Double, page: Int) =
    ElementRepository.getPreviewElements(searchString, types, radius, page)