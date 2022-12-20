package sig.g

import org.junit.Before

abstract class BaseApplicationTest {
    @Before
    fun prepare() {
        System.setProperty("test", "true")

    }
}