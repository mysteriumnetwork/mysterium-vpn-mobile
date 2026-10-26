package updated.mysterium.vpn.model.payment

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The country screen only collects a currency for the gateways that actually
 * offer the user a choice. Requiring one anywhere else leaves Confirm disabled
 * forever, because nothing on the screen can ever set it.
 */
class CurrencySelectionPolicyTest {

    @Test
    fun `card gateways let the user pick a currency`() {
        assertTrue(requiresCurrencySelection(Gateway.STRIPE))
        assertTrue(requiresCurrencySelection(Gateway.PAYPAL))
    }

    @Test
    fun `google play always charges in usd`() {
        assertFalse(requiresCurrencySelection(Gateway.GOOGLE))
    }

    @Test
    fun `coingate picks its currency on the crypto screen`() {
        assertFalse(requiresCurrencySelection(Gateway.COINGATE))
    }

    @Test
    fun `a flow without a gateway never offers currencies`() {
        assertFalse(requiresCurrencySelection(null))
    }
}
