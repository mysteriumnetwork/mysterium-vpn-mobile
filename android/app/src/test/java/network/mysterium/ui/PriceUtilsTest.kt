package network.mysterium.ui

import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.service.core.ProposalPaymentRate
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class PriceUtilsDisplayMoneyTest(private val value: ProposalPaymentMoney, private val expected: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(ProposalPaymentMoney(50000.0, "MYST"), "0.0005")
            )
        }
    }

    @Test
    fun testDisplayValueFormatting() {
        val res = PriceUtils.displayMoney(value)
        assertEquals(expected, res)
    }
}

@RunWith(Parameterized::class)
class PriceUtilsPricePerMinute(private val value: ProposalPaymentMethod, private val expectedAmount: Double, private val expectedCurrency: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(
                            ProposalPaymentMethod(
                                    type = "BYTES_TRANSFERRED_WITH_TIME",
                                    rate = ProposalPaymentRate(
                                            perSeconds = 60,
                                            perBytes = 7_669_584
                                    ),
                                    price = ProposalPaymentMoney(
                                            amount = 50_000.0,
                                            currency = "MYSTT"
                                    )
                            ),
                            50000,
                            "MYSTT"
                    )
            )
        }
    }

    @Test
    fun testPricePerMinute() {
        val price = PriceUtils.pricePerMinute(value)
        assertEquals(expectedAmount, price.amount, 0.0)
        assertEquals(expectedCurrency, price.currency)
    }
}

@RunWith(Parameterized::class)
class PriceUtilsPricePerGiB(private val value: ProposalPaymentMethod, private val expectedAmount: Double, private val expectedCurrency: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(
                            ProposalPaymentMethod(
                                    type = "BYTES_TRANSFERRED_WITH_TIME",
                                    rate = ProposalPaymentRate(
                                            perSeconds = 60_000_000_000,
                                            perBytes = 7_669_584
                                    ),
                                    price = ProposalPaymentMoney(
                                            amount = 50_000.0,
                                            currency = "MYSTT"
                                    )
                            ),
                            7000000,
                            "MYSTT"
                    )
            )
        }
    }

    @Test
    fun testPricePerGiB() {
        val res = PriceUtils.pricePerGiB(value)
        assertEquals(expectedAmount, res.amount, 0.0)
        assertEquals(expectedCurrency, res.currency)
    }
}