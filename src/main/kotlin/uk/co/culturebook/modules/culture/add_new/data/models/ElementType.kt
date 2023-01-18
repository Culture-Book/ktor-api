package uk.co.culturebook.modules.culture.add_new.data.models

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

fun String.decodeElementType(startLocation: Location?, startDateTime: LocalDateTime?) = when {
    equals(ElementType.Food.toString()) -> ElementType.Food
    equals(ElementType.PoI.toString()) -> ElementType.PoI
    equals(ElementType.Music.toString()) -> ElementType.Music
    equals(ElementType.Story.toString()) -> ElementType.Story
    startLocation != null && startDateTime != null -> ElementType.Event(startDateTime, startLocation)
    else -> throw IllegalArgumentException()
}
