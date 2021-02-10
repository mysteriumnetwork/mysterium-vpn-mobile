package network.mysterium.wallet

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TokenModelTest(private val token: Double, private val display: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(124.000, "124.000 MYSTT"),
                    arrayOf(124.540, "124.540 MYSTT"),
                    arrayOf(114.340, "114.340 MYSTT"),
                    arrayOf(123.010, "123.010 MYSTT")
            )
        }
    }

    @Test
    fun testDisplayValueFormatting() {
        val model = TokenModel(token = token, "MYSTT")
        assertEquals(display, model.displayValue)
    }
}
