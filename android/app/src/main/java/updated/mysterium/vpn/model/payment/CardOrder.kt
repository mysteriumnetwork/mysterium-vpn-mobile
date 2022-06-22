package updated.mysterium.vpn.model.payment

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class CardOrder(
    @SerializedName("receive_myst")
    val receiveMyst: Double,
    @SerializedName("items_sub_total")
    val payAmount: Double,
    @SerializedName("tax_sub_total")
    val taxes: Double,
    @SerializedName("order_total")
    val orderTotalAmount: Double,
    @SerializedName("public_gateway_data")
    var pageHtml: PageHtml
) {

    companion object {

        fun fromJSON(json: String): CardOrder? {
            return Gson().fromJson(json, CardOrder::class.java)
        }
    }
}
