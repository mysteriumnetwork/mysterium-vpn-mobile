package updated.mysterium.vpn.model.payment

enum class PaymentOption(val value: String) {

    GOOGLE("google"),
    COINGATE("coingate"),
    PAYPAL("paypal"),
    STRIPE("stripe"),
    MYST_TOTAL("myst_total"),
    MYST_ETHEREUM("myst_ethereum"),
    MYST_POLYGON("myst_polygon");

    companion object {
        fun from(gateway: String?): PaymentOption? {
            return values().find {
                it.value == gateway
            }
        }
    }

}
