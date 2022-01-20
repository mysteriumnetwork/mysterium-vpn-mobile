package updated.mysterium.vpn.model.payment

import com.google.gson.annotations.SerializedName

data class OrderOptions(
    val minimum: Double,
    @SerializedName("suggested")
    val amountsSuggestion: List<Int>
)
