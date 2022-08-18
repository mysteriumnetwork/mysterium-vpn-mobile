package updated.mysterium.vpn.model.payment

class CardOrderRequestInfo(
    override val amountUsd: Double,
    val country: String,
    val state: String,
    val currency: String,
    val gateway: String
) : OrderRequestInfo(amountUsd)
