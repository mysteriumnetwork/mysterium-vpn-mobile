package updated.mysterium.vpn.model.payment

data class Purchase(
    val identityAddress: String,
    val gateway: Gateway,
    val googlePurchaseToken: String,
    val googleProductID: String
)
