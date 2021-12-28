package updated.mysterium.vpn.model.payment

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class PaymentGateway(
    val name: String,
    val currencies: List<String>
) {

    companion object {

        fun listFromJSON(json: String): List<PaymentGateway>? {
            val listType = object : TypeToken<List<PaymentGateway?>?>() {}.type
            return Gson().fromJson(json, listType)
        }
    }
}
