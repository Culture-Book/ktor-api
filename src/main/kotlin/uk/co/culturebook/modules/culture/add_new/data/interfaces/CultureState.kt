package uk.co.culturebook.modules.culture.add_new.data.interfaces

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.culture.add_new.data.models.Culture

sealed interface CultureState {
    sealed interface Success : CultureState {
        @Serializable
        data class AddCulture(val culture: Culture) : Success

        @Serializable
        data class GetCultures(val cultures: List<Culture>) : Success

        @Serializable
        data class GetCulture(val culture: Culture) : Success
    }

    @Serializable
    enum class Error : CultureState {
        Generic,
        DuplicateCulture
    }
}