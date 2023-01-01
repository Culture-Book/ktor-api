package io.culturebook.modules.authentication.data.models.interfaces

import kotlinx.serialization.Serializable
import sig.g.modules.authentication.data.models.User

sealed interface UserDetailsState {
    @Serializable
    data class Success(val user: User) : UserDetailsState

    @Serializable
    enum class Error : UserDetailsState {
        Generic,
        TosUpdate,
        PrivacyUpdate;
    }
}