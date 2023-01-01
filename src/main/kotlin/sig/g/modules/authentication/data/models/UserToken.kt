package sig.g.modules.authentication.data.models

import kotlinx.serialization.Serializable
import sig.g.data_access.serializers.LocalDateTimeSerializer
import sig.g.data_access.serializers.UUIDSerializer
import sig.g.modules.authentication.data.models.enums.TokenType
import sig.g.modules.authentication.data.serializers.TokenTypeSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class UserToken(
    @Serializable(with = UUIDSerializer::class)
    val tokenId: UUID = UUID.randomUUID(),
    val userId: String,
    @Serializable(with = UUIDSerializer::class)
    val accessToken: UUID,
)