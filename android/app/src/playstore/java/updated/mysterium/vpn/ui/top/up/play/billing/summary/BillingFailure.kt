package updated.mysterium.vpn.ui.top.up.play.billing.summary

/**
 * A billing problem the top-up screen has to put to the user.
 *
 * [retryable] decides the shape of that: a prompt that re-queries, or a dialog
 * that closes the screen. Without it every failure looked terminal, so a
 * momentary network blip ejected the user from the flow.
 */
data class BillingFailure(val responseCode: Int, val retryable: Boolean)
