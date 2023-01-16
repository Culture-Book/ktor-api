package uk.co.culturebook.modules.cultural.add_new.location.data.interfaces

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.cultural.add_new.location.data.models.Culture

sealed interface LocationState {

    sealed interface Success : LocationState {
        @Serializable
        data class AddCulture(val culture: Culture) : Success

        @Serializable
        data class GetCultures(val cultures: List<Culture>) : Success
    }

    @Serializable
    enum class Error : LocationState {
        Generic,
        DuplicateCulture
    }
}