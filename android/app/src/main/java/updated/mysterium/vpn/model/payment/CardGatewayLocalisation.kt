package updated.mysterium.vpn.model.payment

import com.google.gson.annotations.SerializedName

data class CardGatewayLocalisation(
    @SerializedName("country")
    val country: String
)
