package updated.mysterium.vpn.model.payment

enum class PaymentOption(val value: String, val gateway: Gateway?) {

    GOOGLE("google", Gateway.GOOGLE),
    COINGATE("coingate", Gateway.COINGATE),
    PAYPAL("paypal", Gateway.PAYPAL),
    STRIPE("stripe", Gateway.STRIPE),
    MYST_TOTAL("myst_total", null),
    MYST_ETHEREUM("myst_ethereum", Gateway.COINGATE),
    MYST_POLYGON("myst_polygon", null);

    companion object {
        fun from(gateway: String?): PaymentOption? = values().find { it.value == gateway }
    }

}
