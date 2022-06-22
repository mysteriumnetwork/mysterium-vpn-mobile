package updated.mysterium.vpn.model.payment

import com.squareup.moshi.Json

data class PublicGatewayData(
    @Json(name = "id")
    val id: Long? = null,
    @Json(name = "payment_address")
    val paymentAddress: String? = null,
    @Json(name = "lightning_network")
    val lightningNetwork: Boolean? = null,
    @Json(name = "payment_url")
    val paymentURL: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "expire_at")
    val expireAt: String? = null
)
