package network.mysterium.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import network.mysterium.service.core.NodeRepository
import network.mysterium.vpn.BuildConfig
import java.net.URL

data class VersionResponse(
        @Json(name = "version")
        val version: String
)

class VersionViewModel(private val nodeRepository: NodeRepository): ViewModel() {
    var remoteVersion: String = ""

    fun appVersion(): String {
        return "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
    }

    suspend fun updateRequired(): Boolean {
        try {
            if (BuildConfig.DEBUG) {
                return false
            }

//            val versionJSON = withContext(Dispatchers.IO) {
//                URL("http://localhost:8080").readText()
//            }
//

            // TODO: Use real endpoint when ready.
            val versionJSON = "{\"version\":\"0.31.0\"}"

            val versionResponse = withContext(Dispatchers.Default) {
                Klaxon().parse<VersionResponse>(versionJSON)
            }

            if (versionResponse == null) {
                return false
            }

            remoteVersion = versionResponse.version
            val remoteVersionNumber = remoteVersion.replace(".", "").toDouble()
            val currentVersionNumber = appVersion().replace(".", "").toDouble()
            return remoteVersionNumber > currentVersionNumber
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check for version", e)
        }
        return false
    }

    companion object {
        const val TAG = "VersionViewModel"
    }
}
