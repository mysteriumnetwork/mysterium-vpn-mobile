package updated.mysterium.vpn.ui.top.up.play.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode
import org.junit.Assert.assertEquals
import org.junit.Test
import updated.mysterium.vpn.ui.top.up.play.billing.summary.MAX_TRANSIENT_QUERY_ATTEMPTS
import updated.mysterium.vpn.ui.top.up.play.billing.summary.QUERY_FAILURE_WINDOW_MILLIS
import updated.mysterium.vpn.ui.top.up.play.billing.summary.QueryFailureResponse
import updated.mysterium.vpn.ui.top.up.play.billing.summary.nextQueryFailureCount
import updated.mysterium.vpn.ui.top.up.play.billing.summary.queryFailureResponse

/**
 * A failed product query is never the same as a gateway with no products, so it
 * is always reported as a billing problem. What varies is whether the user is
 * offered a retry or told to come back later.
 */
class BillingQueryFailurePolicyTest {

    @Test
    fun `user billing errors are terminal on the first failed query`() {
        assertEquals(
            QueryFailureResponse.TERMINAL,
            queryFailureResponse(BillingResponseCode.BILLING_UNAVAILABLE, 1)
        )
        assertEquals(
            QueryFailureResponse.TERMINAL,
            queryFailureResponse(BillingResponseCode.ITEM_UNAVAILABLE, 1)
        )
        assertEquals(
            QueryFailureResponse.TERMINAL,
            queryFailureResponse(BillingResponseCode.DEVELOPER_ERROR, 1)
        )
    }

    @Test
    fun `transient errors stay retryable while the budget lasts`() {
        assertEquals(
            QueryFailureResponse.RETRYABLE,
            queryFailureResponse(BillingResponseCode.NETWORK_ERROR, 1)
        )
        assertEquals(
            QueryFailureResponse.RETRYABLE,
            queryFailureResponse(
                BillingResponseCode.SERVICE_UNAVAILABLE,
                MAX_TRANSIENT_QUERY_ATTEMPTS - 1
            )
        )
    }

    @Test
    fun `transient errors turn terminal once the budget is spent`() {
        assertEquals(
            QueryFailureResponse.TERMINAL,
            queryFailureResponse(
                BillingResponseCode.NETWORK_ERROR,
                MAX_TRANSIENT_QUERY_ATTEMPTS
            )
        )
        assertEquals(
            QueryFailureResponse.TERMINAL,
            queryFailureResponse(
                BillingResponseCode.SERVICE_UNAVAILABLE,
                MAX_TRANSIENT_QUERY_ATTEMPTS + 1
            )
        )
    }

    @Test
    fun `failures close together spend the same budget`() {
        assertEquals(3, nextQueryFailureCount(2, QUERY_FAILURE_WINDOW_MILLIS))
    }

    @Test
    fun `a failure long after the last one starts over`() {
        assertEquals(1, nextQueryFailureCount(5, QUERY_FAILURE_WINDOW_MILLIS + 1))
    }
}
