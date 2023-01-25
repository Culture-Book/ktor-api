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
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.culture.add_new.data.database.repositories.CultureRepository.insertCulture
import uk.co.culturebook.modules.culture.add_new.data.database.tables.Cultures
import uk.co.culturebook.modules.culture.add_new.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.add_new.data.models.Culture
import uk.co.culturebook.modules.culture.add_new.data.models.Location
import uk.co.culturebook.modules.culture.add_new.logic.addCulture
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class CultureRepositoryTests {
    private val dbUrl = "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;"
    private val dbDriver = "org.h2.Driver"
    private val location1 = Location(48.8588443, 2.2943506)
    private val location2 = Location(48.8588443, 2.1943506)
    private val location3 = Location(0.0, 0.0)
    private val culture1 = Culture(UUID.randomUUID(), "Opera1", location1)
    private val culture2 = Culture(UUID.randomUUID(), "Opera2", location2)
    private val culture3 = Culture(UUID.randomUUID(), "Opera3", location3)

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val dbConfig = HikariDataSource(HikariConfig().apply {
        jdbcUrl = dbUrl
        driverClassName = dbDriver
        connectionInitSql =
            "CREATE ALIAS IF NOT EXISTS DISTANCE_IN_KM DETERMINISTIC FOR 'uk.co.culturebook.utils.DistanceUtilsKt.getDistanceInKm';" +
                    "CREATE ALIAS IF NOT EXISTS MY_SIMILARITY DETERMINISTIC FOR 'uk.co.culturebook.utils.SearchUtilsKt.matchStrings';"
        validate()
    })
    private val db = Database.connect(datasource = dbConfig)

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                create(Cultures)
                insertCulture(culture1)
                insertCulture(culture2)
                insertCulture(culture3)
            }
        }
    }

    @After
    fun tearDown() {
        testSuspend(Dispatchers.Main) {
            newSuspendedTransaction {
                drop(Cultures)
            }
        }
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testGetCulture() = testSuspend(Dispatchers.Main) {
        val retrievedCulture = CultureRepository.getCulture(culture1.id!!)
        assertNotNull(retrievedCulture)
        assertEquals(culture1, retrievedCulture)
    }

    @Test
    fun testGetCulturesByLocation() = testSuspend(Dispatchers.Main) {
        val retrievedCultures = CultureRepository.getCulturesByLocation(location1, 10.0)
        assertEquals(2, retrievedCultures.size)
        assertEquals(culture1, retrievedCultures.first())
    }

    @Test
    fun testInsertCulture() = testSuspend(Dispatchers.Main) {
        val newCulture = Culture(UUID.randomUUID(), "Opera in the Park", location1)
        val insertedCulture = insertCulture(newCulture)
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
        val state = addCulture(culture1_new)
        assertEquals(CultureState.Error.DuplicateCulture, state)
    }
}
