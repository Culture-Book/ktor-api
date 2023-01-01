package sig.g.modules.authentication.data.models

sealed interface JwtClaim {
    val claim: String

    object UserId : JwtClaim {
        override val claim: String = "userId"
    }

    object AccessToken : JwtClaim {
        override val claim: String = "accessToken"
    }

}
