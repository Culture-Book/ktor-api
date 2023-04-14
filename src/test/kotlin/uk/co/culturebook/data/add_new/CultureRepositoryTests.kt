package uk.co.culturebook.data.add_new

import io.ktor.test.dispatcher.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.co.culturebook.base.BaseTest
import uk.co.culturebook.modules.authentication.data.database.repositories.UserRepository
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.culture.add_new.logic.addCulture
import uk.co.culturebook.modules.culture.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.culture.data.database.repositories.CultureRepository.insertCulture
import uk.co.culturebook.modules.culture.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.data.models.Culture
import uk.co.culturebook.modules.culture.data.models.Location
import java.util.*

class CultureRepositoryTests : BaseTest() {
    private val user: User = User(email = "email@email.com", password = "password")
    private val location1 = Location(48.8588443, 2.2943506)
    private val location2 = Location(48.8588443, 2.1943506)
    private val location3 = Location(0.0, 0.0)
    private val culture1 = Culture(UUID.randomUUID(), "Opera1", location1)
    private val culture2 = Culture(UUID.randomUUID(), "Opera2", location2)
    private val culture3 = Culture(UUID.randomUUID(), "Opera3", location3)

    @Before
    override fun setUp() {
        super.setUp()
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                UserRepository.registerUser(user)
                insertCulture(culture1, user.userId)
                insertCulture(culture2, user.userId)
                insertCulture(culture3, user.userId)
            }
        }
    }

    @Test
    fun testGetCulture() = testSuspend(Dispatchers.Main) {
        val retrievedCulture = CultureRepository.getCulture(culture1.id!!)
        assertNotNull(retrievedCulture)
        assertEquals(culture1, retrievedCulture)
    }

    @Test
    fun testGetCulturesByLocation() = testSuspend(Dispatchers.Main) {
        val retrievedCultures = CultureRepository.getCulturesByLocation("", location1, 1.0)
        assertNotNull(retrievedCultures)
        assertEquals(1, retrievedCultures.size)
        assertTrue(retrievedCultures.contains(culture1))
    }

    @Test
    fun testInsertCulture() = testSuspend(Dispatchers.Main) {
        val newCulture = Culture(UUID.randomUUID(), "Opera in the Park", location1)
        val insertedCulture = insertCulture(newCulture, user.userId)
        assertNotNull(insertedCulture)
        assertEquals(newCulture, insertedCulture)
    }

    @Test
    fun testDeleteCulture() = testSuspend(Dispatchers.Main) {
        val deleted = CultureRepository.deleteCulture(culture1.id!!)
        assertTrue(deleted)
        val retrievedCulture = CultureRepository.getCulture(culture1.id!!)
        assertNull(retrievedCulture)
    }

    @Test
    fun testUpdateCulture() = testSuspend(Dispatchers.Main) {
        val newName = "Opera in the Park"
        val updatedCulture = culture1.copy(name = newName)
        val result = CultureRepository.updateCulture(updatedCulture)
        assertTrue(result)
        val retrievedCulture = CultureRepository.getCulture(culture1.id!!)
        assertNotNull(retrievedCulture)
        assertEquals(newName, retrievedCulture?.name)
    }

    @Test
    fun testInsertDuplicateCulture() = testSuspend(Dispatchers.Main) {
        val culture1_new = Culture(UUID.randomUUID(), "Opera1", location1)
        val state = addCulture("", culture1_new, location1)
        assertEquals(CultureState.Error.DuplicateCulture, state)
    }
}
