package updated.mysterium.vpn.model.payment

enum class Gateway(val gateway: String) {
    COINGATE("coingate"),
    PAYPAL("paypal"),
    PLAY_BILLING("play_billing");

    companion object {
        fun from(gateway: String?): Gateway? {
            return values().find { it.gateway == gateway }
        }
    }

}
