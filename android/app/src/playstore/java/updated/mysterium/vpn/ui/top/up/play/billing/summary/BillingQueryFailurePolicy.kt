package updated.mysterium.vpn.ui.top.up.play.billing.summary

/**
 * Transient query failures tolerated before the user is told to come back later
 * rather than offered another retry.
 *
 * Nothing spends this budget automatically: a failed product query has no
 * backoff loop behind it, so each attempt is a user reopening the screen or
 * pressing retry.
 */
internal const val MAX_TRANSIENT_QUERY_ATTEMPTS = 3

/**
 * How long a failure keeps counting towards the budget.
 *
 * Without a window the count only ever falls on a successful query, so a user
 * who exhausted it once would be one blip away from a terminal dialog for the
 * rest of the process, with no quiet retry left.
 */
internal const val QUERY_FAILURE_WINDOW_MILLIS = 5L * 60L * 1000L

/** How a failed product query should be put to the user. */
internal enum class QueryFailureResponse {
    /** Worth another go: offer a retry that re-queries. */
    RETRYABLE,

    /** Nothing the user does here will help; say billing is unavailable. */
    TERMINAL
}

/**
 * Folds a new failure into the running count, starting over when the previous
 * one is old enough that the two are unrelated.
 */
internal fun nextQueryFailureCount(previousCount: Int, millisSinceLastFailure: Long): Int =
    if (millisSinceLastFailure > QUERY_FAILURE_WINDOW_MILLIS) 1 else previousCount + 1

/**
 * How to report a failed product query.
 *
 * A non-OK query says nothing about which products exist, so its empty list
 * must never reach the screen: rendered, it reads as "no top-up amounts for
 * this gateway" and sends the user and support after a Play Console problem
 * that isn't there. Every failure is therefore reported as a billing problem.
 *
 * What varies is the way out. Terminal errors (`BILLING_UNAVAILABLE`,
 * `ITEM_UNAVAILABLE`, `DEVELOPER_ERROR`, ...) will not fix themselves, so a
 * retry prompt would only waste the user's time. Transient ones usually clear,
 * so the screen keeps a retry until the budget runs out.
 *
 * @see <a href="https://developer.android.com/google/play/billing/errors">Handle BillingResult response codes</a>
 */
internal fun queryFailureResponse(responseCode: Int, consecutiveFailures: Int): QueryFailureResponse =
    if (responseCode in transientBillingFailures &&
        consecutiveFailures < MAX_TRANSIENT_QUERY_ATTEMPTS
    ) {
        QueryFailureResponse.RETRYABLE
    } else {
        QueryFailureResponse.TERMINAL
    }
