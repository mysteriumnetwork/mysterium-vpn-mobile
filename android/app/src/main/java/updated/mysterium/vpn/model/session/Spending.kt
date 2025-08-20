package updated.mysterium.vpn.model.session

import com.google.gson.annotations.SerializedName

data class Spending(
    @SerializedName("ip_type") val nodeType: String,
    @SerializedName("provider_country") val countryName: String,
    @SerializedName("duration") val duration: Long,
    @SerializedName("tokens") val tokenSpend: Long,
    @SerializedName("created_at") val started: String,
    @SerializedName("bytes_received") val dataSize: Long,
    @SerializedName("direction") val quality: String
)
