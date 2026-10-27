package updated.mysterium.vpn.model.payment

/**
 * Whether the country screen should wait for a currency before enabling Confirm.
 *
 * Only the card gateways put a currency choice in front of the user there. The
 * others settle it elsewhere, so requiring one leaves Confirm disabled with
 * nothing on the screen able to enable it:
 *
 *  - [Gateway.GOOGLE] is always charged in USD. The order request hardcodes it
 *    and the Play summary screen never reads the currency extra, so the value
 *    was only ever gating the button on an unused gateway lookup.
 *  - [Gateway.COINGATE] picks its currency on the separate crypto screen.
 *  - A null gateway is a flow that shows no currencies at all.
 */
fun requiresCurrencySelection(gateway: Gateway?): Boolean = when (gateway) {
    Gateway.PAYPAL, Gateway.STRIPE -> true
    Gateway.GOOGLE, Gateway.COINGATE, null -> false
}
