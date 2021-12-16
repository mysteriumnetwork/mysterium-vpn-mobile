package updated.mysterium.vpn.model.payment

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class CardOrder(
    @SerializedName("items_sub_total")
    val payAmount: Double,
    @SerializedName("tax_sub_total")
    val taxes: Double,
    @SerializedName("order_total")
    val orderTotalAmount: Double
) {

    companion object {

        fun fromJSON(json: String): CardOrder? {
            return Gson().fromJson(json, CardOrder::class.java)
        }
    }
}
