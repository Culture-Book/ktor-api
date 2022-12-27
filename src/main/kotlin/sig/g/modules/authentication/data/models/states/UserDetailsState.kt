package sig.g.modules.authentication.data.models.states

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