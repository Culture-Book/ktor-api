package uk.co.culturebook.modules.culture.add_new.location.logic

import uk.co.culturebook.modules.culture.add_new.location.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.culture.add_new.location.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.add_new.location.data.models.Location
import java.util.*

internal suspend fun getCulturesByLocation(location: Location) =
    CultureState.Success.GetCultures(CultureRepository.getCulturesByLocation(location))

internal suspend fun getCultureById(id: UUID) =
    CultureRepository.getCulture(id)?.let { culture -> CultureState.Success.GetCulture(culture) }
        ?: CultureState.Error.Generic