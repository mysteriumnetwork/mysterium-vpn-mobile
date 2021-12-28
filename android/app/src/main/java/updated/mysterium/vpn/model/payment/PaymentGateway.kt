package updated.mysterium.vpn.model.payment

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class PaymentGateway(
    val name: String,
    @SerializedName("currencies")
    private val currencyRows: List<String>
) {

    fun getCurrencies() = currencyRows.map {
        PaymentCurrency.from(it)
    }

    companion object {

        fun listFromJSON(json: String): List<PaymentGateway>? {
            val listType = object : TypeToken<List<PaymentGateway?>?>() {}.type
            return Gson().fromJson(json, listType)
        }
    }
}
