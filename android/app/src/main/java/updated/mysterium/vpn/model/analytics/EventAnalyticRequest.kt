package updated.mysterium.vpn.model.analytics

import com.google.gson.annotations.SerializedName

data class EventAnalyticRequest(
    @SerializedName("name")
    override val eventName: String,
    @SerializedName("duration")
    override val duration: Long? = null,
    @SerializedName("balance")
    override val balance: Double?,
    @SerializedName("country")
    override val country: String?,
    @SerializedName("provider_id")
    override val providerID: String?,
    @SerializedName("page_title")
    override val pageTitle: String?,
    @SerializedName("client")
    val clientInfo: ClientInfo?
) : EventInfo
