package uk.co.culturebook.base

import uk.co.culturebook.modules.culture.data.models.*
import java.util.*

object MockData {
    val location1 = Location(51.0, 0.0)
    val location2 = Location(51.0, 10.0)

    val culture1 = Culture(name = "Culture 1", location = location1, id = UUID.randomUUID())
    val culture2 = Culture(name = "Culture 2", location = location2, id = UUID.randomUUID())
    val cultureRequest1 = CultureRequest(culture1, location1)
    val cultureRequest2 = CultureRequest(culture2, location2)

    val element1 = Element(
        name = "Element 1",
        information = "Element 1 description",
        location = location1,
        type = ElementType.Food,
        cultureId = culture1.id!!
    )
    val differentNameElement1 = Element(
        name = "Different Name",
        information = "Element 1 description",
        location = location1,
        type = ElementType.Food,
        cultureId = culture1.id!!
    )
    val element2 = Element(
        name = "Element 2",
        information = "Element 2 description",
        location = location2,
        type = ElementType.Food,
        cultureId = culture2.id!!
    )

    val searchCriteria1 = SearchCriteria(
        location = location1,
        radius = 10.0,
        types = listOf(ElementType.Food)
    )
    val noTypesCriteria = SearchCriteria(
        location = location1,
        radius = 10.0,
        types = listOf()
    )

    val getElement1Criteria = SearchCriteria(
        location = location1,
        radius = 10.0,
        types = listOf(ElementType.Food),
        searchString = "Element 1"
    )

    val reactionRequest = RequestReaction(
        elementId = element1.id,
        reaction = Reaction(reaction = "👍")
    )

    val commentRequest = RequestComment(
        elementId = element1.id,
        comment = Comment(comment = "This is a comment")
    )

    val blockedElement = BlockedElement(element1.id)
}