package updated.mysterium.vpn.model.payment

import com.squareup.moshi.Json

data class PublicGatewayData(
    @Json(name = "id")
    val id: String?,
    @Json(name = "checkout_url")
    val checkoutUrl: String?,
    @Json(name = "payment_address")
    val paymentAddress: String?,
    @Json(name = "lightning_network")
    val lightningNetwork: Boolean?,
    @Json(name = "payment_url")
    val paymentURL: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "expire_at")
    val expireAt: String?
)
