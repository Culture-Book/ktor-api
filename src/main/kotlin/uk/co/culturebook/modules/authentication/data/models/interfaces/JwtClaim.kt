package uk.co.culturebook.modules.authentication.data.models.interfaces

sealed interface JwtClaim {
    val claim: String

    object UserId : JwtClaim {
        override val claim: String = "userId"
    }

    object AccessToken : JwtClaim {
        override val claim: String = "accessToken"
    }

}
