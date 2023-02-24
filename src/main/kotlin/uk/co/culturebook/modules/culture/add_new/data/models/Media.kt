package uk.co.culturebook.modules.culture.add_new.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.URISerializer
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.net.URI
import java.util.*

@Serializable
data class Media(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    @Serializable(with = URISerializer::class)
    val uri: URI
)
