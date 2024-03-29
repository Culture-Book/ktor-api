package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.UUIDSerializer
import java.util.*

@Serializable
data class Culture(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    val name: String,
    val location: Location,
    val favourite: Boolean = false,
    val isVerified: Boolean = false
)
