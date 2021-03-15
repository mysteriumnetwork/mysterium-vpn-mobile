package updated.mysterium.vpn.model.session

import com.google.gson.annotations.SerializedName
import network.mysterium.service.core.Identity

data class Session(
    @SerializedName("ProviderID") val providerId: Identity,
    @SerializedName("ProviderCountry") val providerCountry: String,
    @SerializedName("Started") val timeStarted: String,
    @SerializedName("Updated") val timeUpdated: String,
    @SerializedName("DataReceived") val dataReceived: Long
)
