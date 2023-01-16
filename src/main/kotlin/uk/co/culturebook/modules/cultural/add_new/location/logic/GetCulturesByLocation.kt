package uk.co.culturebook.modules.cultural.add_new.location.logic

import uk.co.culturebook.modules.cultural.add_new.location.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.cultural.add_new.location.data.interfaces.LocationState
import uk.co.culturebook.modules.cultural.add_new.location.data.models.Location

internal suspend fun getCulturesByLocation(location: Location) =
    LocationState.Success.GetCultures(CultureRepository.getCulturesByLocation(location))