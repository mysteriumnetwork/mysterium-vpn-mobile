package updated.mysterium.vpn.model.payment

enum class Gateway(val gateway: String) {
    COINGATE("coingate"),
    CARDINITY("cardinity"),
    PAYPAL("paypal"),
    STRIPE("stripe");

    companion object {
        fun from(gateway: String?): Gateway {
            return values().find { it.gateway == gateway } ?: COINGATE
        }
    }

}
