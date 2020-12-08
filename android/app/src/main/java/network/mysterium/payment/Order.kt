package network.mysterium.payment

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon

data class Order constructor(
        @Json("id")
        val id: Long,
        @Json("identity_address")
        val identity: String,
        @Json("status")
        val status: String,
        @Json("myst_amount")
        val mystAmount: Double,
        @Json("pay_amount")
        val payAmount: Double? = null,
        @Json("pay_currency")
        val payCurrency: String? = null,
        @Json("payment_address")
        val paymentAddress: String,
        @Json("payment_url")
        val paymentURL: String,
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
        val klaxon = Klaxon()

        fun fromJSON(json: String): Order? {
            return klaxon.parse<Order>(json)
        }

        fun listFromJSON(json: String): List<Order>? {
            return klaxon.parseArray(json)
        }
    }
}
