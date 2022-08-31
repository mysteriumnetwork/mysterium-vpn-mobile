package updated.mysterium.vpn.model.payment

class PlayBillingOrderRequestInfo(
    override val amountUsd: Double,
    override val country: String,
    override val state: String,
) : OrderRequestInfo(amountUsd, country, state)
