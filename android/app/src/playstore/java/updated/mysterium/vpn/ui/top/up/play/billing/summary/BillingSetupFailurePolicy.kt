package updated.mysterium.vpn.ui.top.up.play.billing.summary

import com.android.billingclient.api.BillingClient.BillingResponseCode

/**
 * Consecutive transient failures tolerated before the user is told billing is
 * unavailable. With the existing exponential backoff (1s, 2s, 4s, ...) this is
 * roughly seven seconds of quiet retrying.
 */
internal const val MAX_TRANSIENT_SETUP_ATTEMPTS = 3

/**
 * Setup failures that Google classifies as transient service problems, usually a
 * network issue between the device and Play. The documented handling is to retry
 * with backoff, not to tell the user billing is unavailable.
 */
private val transientSetupFailures = setOf(
    BillingResponseCode.SERVICE_UNAVAILABLE,
    BillingResponseCode.SERVICE_DISCONNECTED,
    BillingResponseCode.NETWORK_ERROR
)

/**
 * Whether a failed `onBillingSetupFinished` should be shown to the user.
 *
 * Google splits setup failures in two:
 *
 *  - **User billing errors** (`BILLING_UNAVAILABLE`, `DEVELOPER_ERROR`, ...) will
 *    not fix themselves. Automatic retries are explicitly discouraged, so these
 *    are surfaced on the first failure.
 *  - **Transient service errors** (`SERVICE_UNAVAILABLE`, `SERVICE_DISCONNECTED`,
 *    `NETWORK_ERROR`) usually clear by themselves and should be retried with
 *    backoff. Previously the very first of these popped a terminal dialog whose
 *    only button closed the top-up screen, so a blip that would have resolved in
 *    a second or two ejected the user from the flow entirely.
 *
 * @see <a href="https://developer.android.com/google/play/billing/errors">Handle BillingResult response codes</a>
 */
internal fun shouldSurfaceSetupFailure(responseCode: Int, consecutiveFailures: Int): Boolean =
    if (responseCode in transientSetupFailures) {
        consecutiveFailures >= MAX_TRANSIENT_SETUP_ATTEMPTS
    } else {
        true
    }
