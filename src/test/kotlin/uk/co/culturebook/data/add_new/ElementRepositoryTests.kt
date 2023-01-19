package uk.co.culturebook.data.add_new

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.test.dispatcher.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.ElementRepository.insertElement
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Elements
import uk.co.culturebook.modules.culture.add_new.data.models.Element
import uk.co.culturebook.modules.culture.add_new.data.models.ElementType
import uk.co.culturebook.modules.culture.add_new.data.models.Location
import java.time.LocalDateTime
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class ElementRepositoryTests {
    private val dbUrl = "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;"
    private val dbDriver = "org.h2.Driver"
    private val location1 = Location(48.8588443, 2.2943506)
    private val location2 = Location(48.8588443, 2.1943506)
    private val location3 = Location(0.0, 0.1)
    private val location4 = Location(1.0, 1.2)
    private val location5 = Location(1.0, 1.1)
    private val element1 = Element(UUID.randomUUID(), "Opera1", ElementType.Food(), location1, "Information")
    private val element2 = Element(UUID.randomUUID(), "Opera2", ElementType.Music(), location2, "Information")
    private val element3 = Element(UUID.randomUUID(), "Opera3", ElementType.PoI(), location3, "Information")
    private val element4 = Element(UUID.randomUUID(), "Opera4", ElementType.Story(), location4, "Information")
    private val element5 = Element(
        UUID.randomUUID(),
        "Opera5",
        ElementType.Event(LocalDateTime.parse("2019-01-21T05:47:20.949"), location3),
        location5,
        "Information"
    )

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val dbConfig = HikariDataSource(HikariConfig().apply {
        jdbcUrl = dbUrl
        driverClassName = dbDriver
        connectionInitSql =
            "CREATE ALIAS IF NOT EXISTS MY_SIMILARITY DETERMINISTIC FOR 'uk.co.culturebook.utils.SearchUtilsKt.matchStrings';"
        validate()
    })
    private val db = Database.connect(datasource = dbConfig)

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                create(Elements)
                insertElement(element1)
                insertElement(element2)
                insertElement(element3)
                insertElement(element4)
                insertElement(element5)
            }
        }
    }

    @After
    fun tearDown() {
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                drop(Elements)
            }
        }
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testGetElement() = testSuspend(Dispatchers.Main) {
        val retrievedElement = ElementRepository.getElement(element1.id)
        assertNotNull(retrievedElement)
        assertEquals(element1, retrievedElement)
    }

    @Test
    fun testGetDuplicateElements() = testSuspend(Dispatchers.Main) {
        val elements = ElementRepository.getDuplicateElement("Opera", ElementType.PoI().name)
        assertEquals(1, elements.size)
        assertEquals(element3, elements.first())

        val elements1 = ElementRepository.getDuplicateElement("What what", ElementType.Music().name)
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
