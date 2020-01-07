package network.mysterium.ui

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TokenModelTest(private val token: Long, private val display: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(124_00_000_000L, "124.000 MYSTT"),
                    arrayOf(124_54_000_000L, "124.540 MYSTT"),
                    arrayOf(114_34_000_000L, "114.340 MYSTT"),
                    arrayOf(123_01_000_000L, "123.010 MYSTT")
            )
        }
    }

    @Test
    fun testDisplayValueFormatting() {
        val model = TokenModel(token = token)
        assertEquals(display, model.displayValue)
    }
}
