package sig.g.data_access.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import sig.g.modules.authentication.AuthError

object AuthErrorSerializer : KSerializer<AuthError> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthError", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AuthError = AuthError.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: AuthError) = encoder.encodeString(value.code)
}