package io.culturebook.modules.authentication.data.models.interfaces

import io.culturebook.modules.authentication.data.models.User
import kotlinx.serialization.Serializable

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