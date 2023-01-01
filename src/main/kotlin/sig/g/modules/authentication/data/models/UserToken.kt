package sig.g.modules.authentication.data.models

import kotlinx.serialization.Serializable
import sig.g.data_access.serializers.UUIDSerializer
import java.util.*

@Serializable
data class UserToken(
    @Serializable(with = UUIDSerializer::class)
    val tokenId: UUID = UUID.randomUUID(),
    val userId: String,
    @Serializable(with = UUIDSerializer::class)
    val accessToken: UUID,
    @Serializable(with = UUIDSerializer::class)
    val refreshToken: UUID? = null,
)