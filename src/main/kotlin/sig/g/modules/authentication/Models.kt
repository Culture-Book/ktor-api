package sig.g.modules.authentication

data class UserSession(val accessToken: String, val refreshToken: String)
