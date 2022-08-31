package updated.mysterium.vpn.model.payment

abstract class OrderRequestInfo(
    open val amountUsd: Double,
    open val country: String,
    open val state: String
)
