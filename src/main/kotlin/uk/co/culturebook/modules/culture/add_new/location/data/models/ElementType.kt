package uk.co.culturebook.modules.culture.add_new.location.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime


sealed interface ElementType {
    @Serializable
    object Food : ElementType

    @Serializable
    object Music : ElementType

    @Serializable
    object Story : ElementType

    @Serializable
    object PoI : ElementType

    @Serializable
    data class Event(
        @Serializable(with = LocalDateTimeSerializer::class) val startDateTime: LocalDateTime,
        val startLocation: Location
    ) : ElementType
}