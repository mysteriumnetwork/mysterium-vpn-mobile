package updated.mysterium.vpn.ui.top.up.play.billing.summary

import com.android.billingclient.api.BillingClient.BillingResponseCode

/**
 * Consecutive transient failures tolerated before the user is told billing is
 * unavailable. With the existing exponential backoff (1s, 2s, 4s, ...) this is
 * roughly seven seconds of quiet retrying.
 */
internal const val MAX_TRANSIENT_SETUP_ATTEMPTS = 3

/**
 * Failures Google classifies as transient and documents as retry-with-backoff,
 * rather than something to tell the user about. Shared by the setup and query
 * policies, which differ only in what spends the retry budget.
 *
 * `ERROR` belongs here despite the name: Google describes it as an internal
 * Play problem that is sometimes transient, and the retry example on the page
 * below is built around exactly this set.
 *
 * `SERVICE_TIMEOUT` is deliberately absent. It is retriable, but Billing
 * Library 6.0.0 and later report that condition as `SERVICE_UNAVAILABLE`, so on
 * the version this app builds against it would be dead weight.
 */
internal val transientBillingFailures = setOf(
    BillingResponseCode.SERVICE_UNAVAILABLE,
    BillingResponseCode.SERVICE_DISCONNECTED,
    BillingResponseCode.NETWORK_ERROR,
    BillingResponseCode.ERROR
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
    if (responseCode in transientBillingFailures) {
        consecutiveFailures >= MAX_TRANSIENT_SETUP_ATTEMPTS
    } else {
        true
    }
