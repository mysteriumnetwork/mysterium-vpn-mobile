package network.mysterium.payment

import com.beust.klaxon.Klaxon
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class Order constructor(
    @SerializedName("id")
    val id: Long,
    @SerializedName("identity_address")
    val identity: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("myst_amount")
    val mystAmount: Long,
    @SerializedName("pay_currency")
    val payCurrency: String? = null,
    @SerializedName("pay_amount")
    val payAmount: Double? = null,
    @SerializedName("payment_address")
    val paymentAddress: String,
    @SerializedName("payment_url")
    val paymentURL: String? = null,
    @SerializedName("expire_at")
    val expireAt: String?,
    @SerializedName("created_at")
    val createdAt: String?
) {
    val created: Boolean
        get() = status in listOf("new", "pending")
            && payAmount != null && payAmount.compareTo(0) > 0
            && !payCurrency.isNullOrEmpty()
    val paid: Boolean
        get() = status in listOf("confirming", "paid")
    val failed: Boolean
        get() = status in listOf("invalid", "expired", "canceled")

    companion object {

        fun fromJSON(json: String): Order? {
            return Gson().fromJson(json, Order::class.java)
        }

        fun listFromJSON(json: String): List<Order>? {
            val listType = object : TypeToken<List<Order?>?>() {}.type
            return Gson().fromJson(json, listType)
        }
    }
}
