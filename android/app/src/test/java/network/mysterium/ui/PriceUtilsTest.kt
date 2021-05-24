package network.mysterium.ui

import updated.mysterium.vpn.core.ProposalPaymentMoney
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import updated.mysterium.vpn.common.data.PriceUtils

@RunWith(Parameterized::class)
class PriceUtilsDisplayMoneyTest(private val value: ProposalPaymentMoney, private val expected: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(ProposalPaymentMoney(0.0005, "MYST"), "0.0005"),
                    arrayOf(ProposalPaymentMoney(0.00007, "MYST"), "0.00007")
            )
        }
    }

    @Test
    fun testDisplayValueFormatting() {
        val res = PriceUtils.displayMoney(value)
        assertEquals(expected, res)
    }
}
