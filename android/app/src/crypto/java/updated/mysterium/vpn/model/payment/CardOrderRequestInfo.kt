package updated.mysterium.vpn.model.payment

class CardOrderRequestInfo(
    override val amountUsd: Double,
    override val country: String,
    override val state: String,
    val currency: String,
    val gateway: String
) : OrderRequestInfo(amountUsd, country, state)
