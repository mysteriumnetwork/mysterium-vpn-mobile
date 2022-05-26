package updated.mysterium.vpn.model.payment

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class Order constructor(
    @Json(name = "id")
    val id: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "identity")
    val identity: String,
    @Json(name = "channel_address")
    val channelAddress: String,
    @Json(name = "gateway")
    val gateway: String,
    @Json(name = "receive_myst")
    val receiveMyst: String,
    @Json(name = "pay_amount")
    val payAmount: String?,
    @Json(name = "pay_currency")
    val payCurrency: String?,
    @Json(name = "country")
    val country: String,
    @Json(name = "currency")
    val currency: String,
    @Json(name = "items_sub_total")
    val itemsSubTotal: String,
    @Json(name = "tax_rate")
    val taxRate: String,
    @Json(name = "tax_sub_total")
    val taxSubTotal: String,
    @Json(name = "order_total")
    val orderTotal: String,
    @Json(name = "public_gateway_data")
    val publicGatewayData: PublicGatewayData
) {
    val created: Boolean
        get() = status in listOf("new", "pending")
                && payAmount != null && payAmount.toLong() > 0
                && !payCurrency.isNullOrEmpty()
    val paid: Boolean
        get() = status in listOf("confirming", "paid")
    val failed: Boolean
        get() = status in listOf("invalid", "expired", "canceled")

    companion object {
        private val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        fun fromJSON(json: String): Order? {
            return kotlin.runCatching {
                moshi
                    .adapter(Order::class.java)
                    .fromJson(json)
            }.getOrNull()
        }

        fun listFromJSON(json: String): List<Order>? {
            return kotlin.runCatching {
                moshi
                    .adapter<List<Order>>(
                        Types.newParameterizedType(
                            MutableList::class.java,
                            Order::class.java
                        )
                    )
                    .fromJson(json)
            }.getOrNull()
        }
    }
}
