package uk.co.culturebook.modules.culture.add_new.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

sealed interface IElementType {
    val type: String
}

enum class ElementType(override val type: String) : IElementType {
    Food("Food"),
    Music("Music"),
    Story("Story"),
    PoI("PoI");
}

@Serializable
data class Event(
    @Serializable(with = LocalDateTimeSerializer::class) val startDateTime: LocalDateTime,
    val startLocation: Location,
    override val type: String = "Event"
) : IElementType

fun String.decodeElementType(startLocation: Location?, startDateTime: LocalDateTime?) = when {
    equals(ElementType.Food.type) -> ElementType.Food
    equals(ElementType.PoI.type) -> ElementType.PoI
    equals(ElementType.Music.type) -> ElementType.Music
    equals(ElementType.Story.type) -> ElementType.Story
    startLocation != null && startDateTime != null -> Event(startDateTime, startLocation)
    else -> throw IllegalArgumentException()
}
