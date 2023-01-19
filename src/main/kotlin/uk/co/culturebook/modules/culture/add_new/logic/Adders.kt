package uk.co.culturebook.modules.culture.add_new.logic

import uk.co.culturebook.modules.culture.add_new.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.add_new.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.add_new.data.models.Culture
import uk.co.culturebook.modules.culture.add_new.data.models.Element

internal suspend fun addCulture(culture: Culture): CultureState {
    val culturesWithSimilarName = CultureRepository.getCulturesByName(culture.name)

    return if (culturesWithSimilarName.isEmpty()) {
        CultureRepository.insertCulture(culture)?.let { CultureState.Success.AddCulture(culture) }
            ?: CultureState.Error.Generic
    } else {
        CultureState.Error.DuplicateCulture
    }
}

internal suspend fun addElement(element: Element) =
    ElementRepository.insertElement(element)