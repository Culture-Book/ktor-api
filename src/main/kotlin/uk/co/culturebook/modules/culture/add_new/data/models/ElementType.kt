package uk.co.culturebook.modules.culture.add_new.data.models

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.serialization.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

sealed interface ElementType {
    val name: String

    @Serializable
    data class Food(override val name: String = Food.name) : ElementType {
        companion object : ElementType {
            override val name = "Food"
        }
    }

    @Serializable
    data class Music(override val name: String = Music.name) : ElementType {
        companion object : ElementType {
            override val name = "Music"
        }
    }

    @Serializable
    data class Story(override val name: String = Story.name) : ElementType {
        companion object : ElementType {
            override val name = "Story"
        }
    }

    @Serializable
    data class PoI(override val name: String = PoI.name) : ElementType {
        companion object : ElementType {
            override val name = "PoI"
        }
    }

    @Serializable
    data class Event(
        @Serializable(with = LocalDateTimeSerializer::class)
        val startDateTime: LocalDateTime,
        val startLocation: Location,
        override val name: String = Event.name
    ) : ElementType {
        companion object : ElementType {
            override val name: String = "Event"
        }
    }

}

fun String.decodeElementType(startLocation: Location?, startDateTime: LocalDateTime?) = when {
    equals(ElementType.Food.name) -> ElementType.Food()
    equals(ElementType.PoI.name) -> ElementType.PoI()
    equals(ElementType.Music.name) -> ElementType.Music()
    equals(ElementType.Story.name) -> ElementType.Story()
    startLocation != null && startDateTime != null -> ElementType.Event(
        startDateTime = startDateTime,
        startLocation = startLocation
    )

    else -> throw IllegalArgumentException()
}

fun String?.isValidElementTypeName() = when {
    equals(ElementType.Food.name) -> true
    equals(ElementType.PoI.name) -> true
    equals(ElementType.Music.name) -> true
    equals(ElementType.Story.name) -> true
    equals(ElementType.Event.name) -> true
    else -> false
}

