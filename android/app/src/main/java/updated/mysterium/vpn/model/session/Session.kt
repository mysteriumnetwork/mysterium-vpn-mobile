package updated.mysterium.vpn.model.session

import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("provider_id") val providerId: String,
    @SerializedName("provider_country") val providerCountry: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("duration") val duration: Long,
    @SerializedName("bytes_received") val dataReceived: Long
)
