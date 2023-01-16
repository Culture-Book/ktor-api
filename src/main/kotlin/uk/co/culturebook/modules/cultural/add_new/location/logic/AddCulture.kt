package uk.co.culturebook.modules.cultural.add_new.location.logic

import uk.co.culturebook.modules.cultural.add_new.location.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.cultural.add_new.location.data.interfaces.LocationState
import uk.co.culturebook.modules.cultural.add_new.location.data.models.Culture

internal suspend fun addCulture(culture: Culture): LocationState {
    val culturesWithSimilarName = CultureRepository.getCulturesByName(culture.name)

    return if (culturesWithSimilarName.isEmpty()) {
        CultureRepository.insertCulture(culture)?.let { LocationState.Success.AddCulture(culture) }
            ?: LocationState.Error.Generic
    } else {
        LocationState.Error.DuplicateCulture
    }
}