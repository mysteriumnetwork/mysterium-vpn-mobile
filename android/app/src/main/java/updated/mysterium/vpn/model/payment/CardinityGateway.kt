package updated.mysterium.vpn.model.payment

import com.google.gson.annotations.SerializedName

data class CardinityGateway(
    @SerializedName("country")
    val country: String
)
