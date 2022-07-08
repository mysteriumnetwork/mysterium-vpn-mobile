package updated.mysterium.vpn.model.identity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class MigrateHermesStatusResponse(
    @SerializedName("status")
    val status: String
) {

    companion object {
        fun fromJSON(json: String): MigrateHermesStatusResponse? {
            return Gson().fromJson(json, MigrateHermesStatusResponse::class.java)
        }
    }

}
