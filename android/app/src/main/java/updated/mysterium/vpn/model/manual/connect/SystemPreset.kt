package updated.mysterium.vpn.model.manual.connect

import com.google.gson.annotations.SerializedName

data class SystemPreset(
    @SerializedName("id") val filterId: Int,
    @SerializedName("name") val title: String
)
