package updated.mysterium.vpn.ui.top.up.play.billing

import com.android.billingclient.api.BillingClient.ConnectionState
import org.junit.Assert.assertEquals
import org.junit.Test
import updated.mysterium.vpn.ui.top.up.play.billing.summary.BillingRefreshAction
import updated.mysterium.vpn.ui.top.up.play.billing.summary.refreshActionFor

/**
 * Starting a second connection while one is in flight makes Play answer
 * `onBillingSetupFinished` with DEVELOPER_ERROR ("Client is already in the
 * process of connecting to billing service."), which the failure policy treats
 * as terminal and shows as "billing unavailable".
 */
class BillingConnectionPolicyTest {

    @Test
    fun `a disconnected client is connected first`() {
        assertEquals(
            BillingRefreshAction.START_CONNECTION,
            refreshActionFor(ConnectionState.DISCONNECTED)
        )
    }

    @Test
    fun `a connected client queries products straight away`() {
        assertEquals(
            BillingRefreshAction.QUERY_PRODUCTS,
            refreshActionFor(ConnectionState.CONNECTED)
        )
    }

    @Test
    fun `a connecting client waits for the setup already in flight`() {
        assertEquals(
            BillingRefreshAction.AWAIT_PENDING_SETUP,
            refreshActionFor(ConnectionState.CONNECTING)
        )
    }

    @Test
    fun `a closed client is reconnected rather than left dead`() {
        assertEquals(
            BillingRefreshAction.START_CONNECTION,
            refreshActionFor(ConnectionState.CLOSED)
        )
    }
}
