package updated.mysterium.vpn.model.payment

enum class Gateway(val gateway: String) {
    GOOGLE("google"),
    COINGATE("coingate"),
    PAYPAL("paypal"),
    STRIPE("stripe");

    companion object {
        fun from(gateway: String?): Gateway? {
            return values().find { it.gateway == gateway }
        }
    }

}
