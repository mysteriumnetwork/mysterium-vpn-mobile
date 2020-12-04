package network.mysterium.payment

import com.beust.klaxon.Json

data class Order(
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
        val paymentAddress: String
) {
    val created: Boolean
        get() = status == "pending"
                && payAmount != null && payAmount.compareTo(0) > 0
                && !payCurrency.isNullOrEmpty()
}
