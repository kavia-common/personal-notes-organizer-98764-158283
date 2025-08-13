package org.example.app

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * PUBLIC_INTERFACE
 * A minimal JUnit4 test to ensure the Gradle unit test task discovers at least one test.
 */
class SanityTest {
    // PUBLIC_INTERFACE
    @Test
    fun addition_isCorrect() {
        /** Verifies basic arithmetic as a sanity check. */
        assertEquals(4, 2 + 2)
    }
}
