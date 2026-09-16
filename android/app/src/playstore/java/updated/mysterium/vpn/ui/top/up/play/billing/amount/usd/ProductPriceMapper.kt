package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

/**
 * Extracts the USD amount from a Play product description formatted as
 * "(5.99 USD)".
 *
 * Returns null when no amount can be parsed, so a malformed or unexpected
 * description skips that product instead of crashing the top-up screen.
 */
fun parseAmountUsd(description: String): Double? = description
    .removeSurrounding("(", ")")
    .substringBefore(' ')
    .toDoubleOrNull()
