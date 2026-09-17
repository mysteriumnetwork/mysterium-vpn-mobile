package updated.mysterium.vpn.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Callbacks invoked by the native node must never let anything escape back into
 * Go; a pending JNI exception aborts the whole process on the next native call.
 */
class NativeCallbackGuardTest {

    @Test
    fun `runs the callback body`() {
        var ran = false
        guardNativeCallback("test") { ran = true }
        assertTrue(ran)
    }

    @Test
    fun `swallows exceptions thrown by the callback`() {
        guardNativeCallback("test") {
            throw IllegalArgumentException("bad route address")
        }
    }

    @Test
    fun `swallows errors thrown by the callback`() {
        guardNativeCallback("test") {
            throw StackOverflowError("deep")
        }
    }

    @Test
    fun `keeps running later callbacks after one fails`() {
        var completed = 0
        guardNativeCallback("first") { throw IllegalStateException("boom") }
        guardNativeCallback("second") { completed++ }
        assertEquals(1, completed)
    }
}
