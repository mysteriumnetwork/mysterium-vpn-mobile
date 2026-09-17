package updated.mysterium.vpn.ui.top.up.play.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import updated.mysterium.vpn.ui.top.up.play.billing.amount.usd.parseAmountUsd

class ProductPriceMapperTest {

    @Test
    fun `parses amount from parenthesised usd description`() {
        assertEquals(5.99, parseAmountUsd("(5.99 USD)")!!, 0.001)
    }

    @Test
    fun `parses amount without parentheses`() {
        assertEquals(1.99, parseAmountUsd("1.99 USD")!!, 0.001)
    }

    @Test
    fun `returns null for a description with no number`() {
        assertNull(parseAmountUsd("Top up your account"))
    }

    @Test
    fun `returns null for an empty description`() {
        assertNull(parseAmountUsd(""))
    }
}
