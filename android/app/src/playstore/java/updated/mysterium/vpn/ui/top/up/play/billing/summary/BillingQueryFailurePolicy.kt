package updated.mysterium.vpn.ui.top.up.play.billing.summary

/**
 * Transient query failures tolerated before the user is told billing is
 * unavailable.
 *
 * Unlike the setup budget, nothing spends this one automatically: a failed
 * product query has no backoff loop behind it, so each attempt is a user
 * reopening the screen or pressing "Try again".
 */
internal const val MAX_TRANSIENT_QUERY_ATTEMPTS = 3

/**
 * Whether a failed product query should be shown as a billing error.
 *
 * A non-OK query says nothing about which products exist, so its empty list must
 * never reach the screen: rendered, it reads as "no top-up amounts for this
 * gateway" and sends the user and support after a Play Console problem that
 * isn't there.
 *
 * The split mirrors [shouldSurfaceSetupFailure]. Terminal errors
 * (`BILLING_UNAVAILABLE`, `ITEM_UNAVAILABLE`, `DEVELOPER_ERROR`, ...) will not
 * fix themselves, so a retry prompt would only waste the user's time. Transient
 * ones usually clear, so the screen keeps its retry until the budget runs out.
 *
 * @see <a href="https://developer.android.com/google/play/billing/errors">Handle BillingResult response codes</a>
 */
internal fun shouldSurfaceQueryFailure(responseCode: Int, consecutiveFailures: Int): Boolean =
    if (responseCode in transientBillingFailures) {
        consecutiveFailures >= MAX_TRANSIENT_QUERY_ATTEMPTS
    } else {
        true
    }
