package uk.co.culturebook.modules.culture.add_new.logic

import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ContributionRepository
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.add_new.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.add_new.data.models.Contribution
import uk.co.culturebook.modules.culture.add_new.data.models.Element
import uk.co.culturebook.modules.culture.add_new.data.models.Location
import java.util.*

internal suspend fun getCulturesByLocation(location: Location) =
    CultureState.Success.GetCultures(CultureRepository.getCulturesByLocation(location))

internal suspend fun getCultureById(id: UUID) =
    CultureRepository.getCulture(id)?.let { culture -> CultureState.Success.GetCulture(culture) }
        ?: CultureState.Error.Generic

internal suspend fun getDuplicateElements(name: String, type: String): List<Element> =
    ElementRepository.getDuplicateElement(name, type)

internal suspend fun getDuplicateContributions(name: String, type: String): List<Contribution> =
    ContributionRepository.getDuplicateContribution(name, type)