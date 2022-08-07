package updated.mysterium.vpn.model.payment

enum class PaymentOption(val gateway: Gateway?) {

    COINGATE(Gateway.COINGATE),
    PAYPAL(Gateway.PAYPAL),
    STRIPE(Gateway.STRIPE),
    MYST_TOTAL(null),
    MYST_ETHEREUM(Gateway.COINGATE),
    MYST_POLYGON(null);

    companion object {
        fun from(gateway: Gateway?): PaymentOption? {
            return when (gateway) {
                Gateway.COINGATE -> COINGATE
                Gateway.PAYPAL -> PAYPAL
                Gateway.STRIPE -> STRIPE
                else -> null
            }
        }
    }

}
