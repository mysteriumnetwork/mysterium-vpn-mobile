package updated.mysterium.vpn.ui.top.up.play.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import updated.mysterium.vpn.ui.top.up.play.billing.summary.MAX_TRANSIENT_SETUP_ATTEMPTS
import updated.mysterium.vpn.ui.top.up.play.billing.summary.isRetryableBillingFailure
import updated.mysterium.vpn.ui.top.up.play.billing.summary.shouldSurfaceSetupFailure

/**
 * Google's guidance: transient service errors should be retried, user billing
 * errors should be put to the user.
 * https://developer.android.com/google/play/billing/errors
 */
class BillingFailurePolicyTest {

    @Test
    fun `transient failures are worth retrying`() {
        assertTrue(isRetryableBillingFailure(BillingResponseCode.SERVICE_UNAVAILABLE))
        assertTrue(isRetryableBillingFailure(BillingResponseCode.SERVICE_DISCONNECTED))
        assertTrue(isRetryableBillingFailure(BillingResponseCode.NETWORK_ERROR))
        // Google documents ERROR as an internal Play problem to retry, and
        // builds its own retry example around exactly this set.
        assertTrue(isRetryableBillingFailure(BillingResponseCode.ERROR))
    }

    @Test
    fun `user billing errors are not worth retrying`() {
        assertFalse(isRetryableBillingFailure(BillingResponseCode.BILLING_UNAVAILABLE))
        assertFalse(isRetryableBillingFailure(BillingResponseCode.ITEM_UNAVAILABLE))
        assertFalse(isRetryableBillingFailure(BillingResponseCode.DEVELOPER_ERROR))
        assertFalse(isRetryableBillingFailure(BillingResponseCode.FEATURE_NOT_SUPPORTED))
    }

    @Test
    fun `setup retries transient failures quietly before alerting the user`() {
        assertFalse(shouldSurfaceSetupFailure(BillingResponseCode.ERROR, 1))
        assertFalse(shouldSurfaceSetupFailure(BillingResponseCode.SERVICE_UNAVAILABLE, 1))
        assertFalse(
            shouldSurfaceSetupFailure(
                BillingResponseCode.NETWORK_ERROR,
                MAX_TRANSIENT_SETUP_ATTEMPTS - 1
            )
        )
    }

    @Test
    fun `setup surfaces transient failures once the retry budget is spent`() {
        assertTrue(
            shouldSurfaceSetupFailure(
                BillingResponseCode.SERVICE_UNAVAILABLE,
                MAX_TRANSIENT_SETUP_ATTEMPTS
            )
        )
    }

    @Test
    fun `setup surfaces user billing errors on the first failure`() {
        assertTrue(shouldSurfaceSetupFailure(BillingResponseCode.BILLING_UNAVAILABLE, 1))
        assertTrue(shouldSurfaceSetupFailure(BillingResponseCode.DEVELOPER_ERROR, 1))
    }
}
