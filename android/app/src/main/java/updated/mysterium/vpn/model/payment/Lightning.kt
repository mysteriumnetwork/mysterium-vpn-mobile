package updated.mysterium.vpn.model.payment

import com.google.gson.annotations.SerializedName

data class Lightning(
    @SerializedName("lightning_network")
    val lightning: Boolean
)
