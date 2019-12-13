package network.mysterium.ui

import org.junit.Assert.*
import org.junit.Test

class TokenModelTest {

    @Test
    fun testDisplayValueFormatting() {
        var model = TokenModel(value = 124_00_000_000L)
        assertEquals("124.000 MYST", model.displayValue)

        model = TokenModel(value = 124_54_000_000L)
        assertEquals("124.540 MYST", model.displayValue)

        model = TokenModel(value = 114_34_000_000L)
        assertEquals("114.340 MYST", model.displayValue)

        model = TokenModel(value = 123_01_000_000L)
        assertEquals("123.010 MYST", model.displayValue)
    }
}