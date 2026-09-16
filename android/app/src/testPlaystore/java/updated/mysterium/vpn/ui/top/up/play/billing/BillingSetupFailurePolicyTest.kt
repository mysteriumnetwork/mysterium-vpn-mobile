package updated.mysterium.vpn.ui.top.up.play.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import updated.mysterium.vpn.ui.top.up.play.billing.summary.MAX_TRANSIENT_SETUP_ATTEMPTS
import updated.mysterium.vpn.ui.top.up.play.billing.summary.shouldSurfaceSetupFailure

/**
 * Google's guidance: transient service errors should be retried with backoff,
 * user billing errors should be surfaced immediately.
 * https://developer.android.com/google/play/billing/errors
 */
class BillingSetupFailurePolicyTest {

    @Test
    fun `user billing errors surface on the first failure`() {
        assertTrue(shouldSurfaceSetupFailure(BillingResponseCode.BILLING_UNAVAILABLE, 1))
        assertTrue(shouldSurfaceSetupFailure(BillingResponseCode.DEVELOPER_ERROR, 1))
        assertTrue(shouldSurfaceSetupFailure(BillingResponseCode.FEATURE_NOT_SUPPORTED, 1))
    }

    @Test
    fun `transient errors are retried silently at first`() {
        assertFalse(shouldSurfaceSetupFailure(BillingResponseCode.SERVICE_UNAVAILABLE, 1))
        assertFalse(shouldSurfaceSetupFailure(BillingResponseCode.SERVICE_DISCONNECTED, 1))
        assertFalse(shouldSurfaceSetupFailure(BillingResponseCode.NETWORK_ERROR, 2))
    }

    @Test
    fun `transient errors surface once the retry budget is exhausted`() {
        assertTrue(
            shouldSurfaceSetupFailure(
                BillingResponseCode.SERVICE_UNAVAILABLE,
                MAX_TRANSIENT_SETUP_ATTEMPTS
            )
        )
        assertTrue(
            shouldSurfaceSetupFailure(
                BillingResponseCode.NETWORK_ERROR,
                MAX_TRANSIENT_SETUP_ATTEMPTS + 1
            )
        )
    }
}
