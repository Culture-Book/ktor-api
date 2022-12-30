package sig.g.modules.authentication.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import sig.g.modules.authentication.data.models.enums.TokenType

object TokenTypeSerializer : KSerializer<TokenType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthState.Error", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TokenType = when (decoder.decodeInt()) {
        TokenType.Origin.ordinal -> TokenType.Origin
        TokenType.Google.ordinal -> TokenType.Google
        else -> throw IllegalArgumentException("Cannot decode TokenType")
    }

    override fun serialize(encoder: Encoder, value: TokenType) = encoder.encodeInt(value.ordinal)
}