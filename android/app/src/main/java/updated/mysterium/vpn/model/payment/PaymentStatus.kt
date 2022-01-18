package updated.mysterium.vpn.model.payment

enum class PaymentStatus(private val raw: String) {

    STATUS_PAID("paid"),
    STATUS_EXPIRED("expired"),
    STATUS_INVALID("invalid"),
    STATUS_CANCELED("canceled"),
    STATUS_REFUNDED("refunded");

    companion object {

        fun from(raw: String) = values().find {
            it.raw == raw
        }
    }
}
