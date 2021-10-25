package updated.mysterium.vpn.model.analytics

import com.google.gson.annotations.SerializedName

data class ClientInfo(
    @SerializedName("machine_id")
    val machineID: String?,
    @SerializedName("app_version")
    val appVersion: String?,
    @SerializedName("os")
    val os: String = "android",
    @SerializedName("os_version")
    val osVersion: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("consumer_id")
    val consumerID: String?
)
