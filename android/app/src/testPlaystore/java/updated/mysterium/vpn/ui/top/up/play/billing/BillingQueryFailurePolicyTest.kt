package updated.mysterium.vpn.ui.top.up.play.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import updated.mysterium.vpn.ui.top.up.play.billing.summary.MAX_TRANSIENT_QUERY_ATTEMPTS
import updated.mysterium.vpn.ui.top.up.play.billing.summary.shouldSurfaceQueryFailure

/**
 * A failed product query is not the same as a gateway with no products, so the
 * two must not reach the user as the same message.
 */
class BillingQueryFailurePolicyTest {

    @Test
    fun `user billing errors surface on the first failed query`() {
        assertTrue(shouldSurfaceQueryFailure(BillingResponseCode.BILLING_UNAVAILABLE, 1))
        assertTrue(shouldSurfaceQueryFailure(BillingResponseCode.ITEM_UNAVAILABLE, 1))
        assertTrue(shouldSurfaceQueryFailure(BillingResponseCode.DEVELOPER_ERROR, 1))
    }

    @Test
    fun `transient errors stay retryable at first`() {
        assertFalse(shouldSurfaceQueryFailure(BillingResponseCode.NETWORK_ERROR, 1))
        assertFalse(shouldSurfaceQueryFailure(BillingResponseCode.SERVICE_UNAVAILABLE, 1))
        assertFalse(shouldSurfaceQueryFailure(BillingResponseCode.SERVICE_DISCONNECTED, 2))
    }

    @Test
    fun `transient errors surface once the retries are spent`() {
        assertTrue(
            shouldSurfaceQueryFailure(
                BillingResponseCode.NETWORK_ERROR,
                MAX_TRANSIENT_QUERY_ATTEMPTS
            )
        )
        assertTrue(
            shouldSurfaceQueryFailure(
                BillingResponseCode.SERVICE_UNAVAILABLE,
                MAX_TRANSIENT_QUERY_ATTEMPTS + 1
            )
        )
    }
}
