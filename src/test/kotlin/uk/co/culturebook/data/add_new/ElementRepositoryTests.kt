package uk.co.culturebook.data.add_new

import io.ktor.test.dispatcher.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.co.culturebook.base.BaseTest
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.culture.data.database.repositories.CultureRepository.insertCulture
import uk.co.culturebook.modules.culture.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.data.database.repositories.ElementRepository.insertElement
import uk.co.culturebook.modules.culture.data.models.*
import java.time.LocalDateTime
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class ElementRepositoryTests : BaseTest() {
    private val user: User = User(email = "email@email.com", password = "password")

    private val location1 = Location(48.8588443, 2.2943506)
    private val location2 = Location(48.8588443, 2.1943506)
    private val location3 = Location(0.0, 0.1)
    private val location4 = Location(1.0, 1.2)
    private val location5 = Location(1.0, 1.1)

    private val culture1 = Culture(UUID.randomUUID(), "Opera1", location1)
    private val culture2 = Culture(UUID.randomUUID(), "Opera2", location2)
    private val culture3 = Culture(UUID.randomUUID(), "Opera3", location3)
    private val culture4 = Culture(UUID.randomUUID(), "Opera4", location4)
    private val culture5 = Culture(UUID.randomUUID(), "Opera5", location5)
    private val element1 =
        Element(UUID.randomUUID(), culture1.id!!, "Opera1", ElementType.Food, location1, "Information")
    private val element2 =
        Element(UUID.randomUUID(), culture2.id!!, "Opera2", ElementType.Music, location2, "Information")
    private val element3 =
        Element(UUID.randomUUID(), culture3.id!!, "Opera3", ElementType.PoI, location3, "Information")
    private val element4 =
        Element(UUID.randomUUID(), culture4.id!!, "Opera4", ElementType.Story, location4, "Information")
    private val element5 = Element(
        UUID.randomUUID(),
        culture5.id!!,
        "Opera5",
        ElementType.Event,
        location5,
        "Information",
        EventType(LocalDateTime.parse("2019-01-21T05:47:20.949"), location3),
    )

    @Before
    override fun setUp() {
        super.setUp()
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                UserRepository.registerUser(user)
                insertCulture(culture1, user.userId)
                insertCulture(culture2, user.userId)
                insertCulture(culture3, user.userId)
                insertCulture(culture4, user.userId)
                insertCulture(culture5, user.userId)

                insertElement(element1, user.userId)
                insertElement(element2, user.userId)
                insertElement(element3, user.userId)
                insertElement(element4, user.userId)
                insertElement(element5, user.userId)
            }
        }
    }

    @Test
    fun testGetElement() = testSuspend(Dispatchers.Main) {
        val retrievedElement = ElementRepository.getElement(element1.id)
        assertNotNull(retrievedElement)
        assertEquals(element1, retrievedElement)
    }

    @Test
    fun testGetDuplicateElements() = testSuspend(Dispatchers.Main) {
        val elements = ElementRepository.getDuplicateElement("Opera", ElementType.PoI.name)
        assertEquals(1, elements.size)
        assertEquals(element3, elements.first())

        val elements1 = ElementRepository.getDuplicateElement("What what", ElementType.Music.name)
        assertEquals(0, elements1.size)
    }

    @Test
    fun testDeleteElement() = testSuspend(Dispatchers.Main) {
        val deleted = ElementRepository.deleteElement(element1.id)
        assertTrue(deleted)
        val retrievedElement = ElementRepository.getElement(element1.id)
        assertNull(retrievedElement)
    }

    @Test
    fun testUpdateElement() = testSuspend(Dispatchers.Main) {
        val newName = "Opera in the Park"
        val updatedElement = element1.copy(name = newName)
        val result = ElementRepository.updateElement(updatedElement)
        assertTrue(result)
        val retrievedElement = ElementRepository.getElement(element1.id)
        assertNotNull(retrievedElement)
        assertEquals(newName, retrievedElement?.name)
    }
}
