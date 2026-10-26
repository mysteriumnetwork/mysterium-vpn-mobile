package updated.mysterium.vpn.ui.top.up.play.billing.summary

import com.android.billingclient.api.BillingClient.ConnectionState

/** What a refresh should do with the billing client it finds. */
internal enum class BillingRefreshAction {
    /** The client is live; ask Play for the products now. */
    QUERY_PRODUCTS,

    /** A setup is already in flight; its callback will query once it lands. */
    AWAIT_PENDING_SETUP,

    /** No usable connection and none pending; open one. */
    START_CONNECTION
}

/**
 * Picks the refresh action for a billing client.
 *
 * `startConnection` is not idempotent. Called while the client is already
 * CONNECTING, Play answers `onBillingSetupFinished` immediately with
 * DEVELOPER_ERROR and the debug message "Client is already in the process of
 * connecting to billing service." That code is a user billing error, so
 * [shouldSurfaceSetupFailure] surfaces it on the first failure and the user is
 * told billing is unavailable, while the connection that was genuinely in
 * flight goes on to succeed a moment later.
 *
 * That collision is the normal case rather than a race: the data source is a
 * lazy Koin singleton, so its constructor opens the first connection inside the
 * very statement that then calls refresh.
 *
 * [isReady] is passed separately because `connectionState` is the weaker test.
 * `isReady()` also requires the service binder and the service connection, so a
 * client whose binder has gone can still report CONNECTED; querying that one
 * fails where reconnecting would have recovered.
 *
 * @see <a href="https://developer.android.com/google/play/billing/errors">Handle BillingResult response codes</a>
 */
internal fun refreshActionFor(connectionState: Int, isReady: Boolean): BillingRefreshAction = when {
    isReady -> BillingRefreshAction.QUERY_PRODUCTS
    connectionState == ConnectionState.CONNECTING -> BillingRefreshAction.AWAIT_PENDING_SETUP
    else -> BillingRefreshAction.START_CONNECTION
}
