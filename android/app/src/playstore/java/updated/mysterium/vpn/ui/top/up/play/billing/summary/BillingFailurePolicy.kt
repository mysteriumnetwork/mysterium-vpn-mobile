package updated.mysterium.vpn.ui.top.up.play.billing.summary

import com.android.billingclient.api.BillingClient.BillingResponseCode

/**
 * Consecutive transient failures tolerated before the user is told billing is
 * unavailable. With the existing exponential backoff (1s, 2s, 4s, ...) this is
 * roughly seven seconds of quiet retrying.
 */
internal const val MAX_TRANSIENT_SETUP_ATTEMPTS = 3

/**
 * Failures Google documents as retry-with-backoff rather than something to tell
 * the user about: usually a service problem between the device and Play.
 *
 * `ERROR` belongs here despite the name. Google describes it as an internal
 * Play problem that is sometimes transient, and builds its own retry example
 * around exactly this set.
 *
 * `SERVICE_TIMEOUT` is deliberately absent. It is retriable, but Billing
 * Library 6.0.0 and later report that condition as `SERVICE_UNAVAILABLE`, so
 * against the version this app builds on it would never arrive.
 *
 * @see <a href="https://developer.android.com/google/play/billing/errors">Handle BillingResult response codes</a>
 */
private val transientBillingFailures = setOf(
    BillingResponseCode.SERVICE_UNAVAILABLE,
    BillingResponseCode.SERVICE_DISCONNECTED,
    BillingResponseCode.NETWORK_ERROR,
    BillingResponseCode.ERROR
)

/**
 * Whether the screen should offer another go at a failure, rather than a dialog
 * that closes the top-up flow.
 *
 * This carries no attempt budget. Nothing retries a failed product query on its
 * own, so every attempt is the user asking for one, and a transient condition
 * is worth another try however many times they ask. A budget here only took the
 * retry away from someone whose connection was coming back.
 */
internal fun isRetryableBillingFailure(responseCode: Int): Boolean =
    responseCode in transientBillingFailures

/**
 * Whether a failed `onBillingSetupFinished` should reach the user at all.
 *
 * Setup is the one path that does retry by itself, on a backoff, so transient
 * failures stay quiet while that runs. Previously the first of them popped a
 * terminal dialog whose only button closed the top-up screen, so a blip that
 * would have cleared in a second ejected the user from the flow.
 *
 * User billing errors will not fix themselves and Google discourages retrying
 * them, so those are surfaced on the first failure.
 */
internal fun shouldSurfaceSetupFailure(responseCode: Int, consecutiveFailures: Int): Boolean =
    if (isRetryableBillingFailure(responseCode)) {
        consecutiveFailures >= MAX_TRANSIENT_SETUP_ATTEMPTS
    } else {
        true
    }
