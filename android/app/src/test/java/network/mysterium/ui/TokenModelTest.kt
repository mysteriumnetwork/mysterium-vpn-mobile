package network.mysterium.ui

import org.junit.Assert.*
import org.junit.Test

class TokenModelTest {

    @Test
    fun testDisplayValueFormatting() {
        var model = TokenModel(value = 124_00_000_000L)
        assertEquals("124.00 MYST", model.displayValue)

        model = TokenModel(value = 124_54_000_000L)
        assertEquals("124.54 MYST", model.displayValue)

        model = TokenModel(value = 114_34_000_000L)
        assertEquals("114.34 MYST", model.displayValue)
    }
}