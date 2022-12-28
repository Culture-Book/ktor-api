package sig.g.modules.authentication.data.models

import kotlinx.serialization.Serializable


@Serializable
data class UserSession(val accessToken: String?, val refreshToken: String? = null)