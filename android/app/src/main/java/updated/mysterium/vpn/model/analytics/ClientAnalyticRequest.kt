package updated.mysterium.vpn.model.analytics

import com.google.gson.annotations.SerializedName

data class ClientAnalyticRequest(
    @SerializedName("name")
    val eventName: String,
    @SerializedName("client")
    val clientInfo: ClientInfo?
)
