package network.mysterium.net

import android.net.*
import android.net.NetworkCapabilities.*
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class NetworkMonitor(
        private val connectivity: ConnectivityManager,
        private val wifi: WifiManager
) : ConnectivityManager.NetworkCallback() {

    companion object {
        private const val TAG = "NetworkMonitor"
    }

    val state = MutableLiveData(NetworkState())

    fun startWatching() {
        connectivity.registerNetworkCallback(NetworkRequest.Builder()
                .addTransportType(TRANSPORT_WIFI)
                .addTransportType(TRANSPORT_CELLULAR)
                .build(),
                this
        )
    }

    override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities?) {
        Log.d(TAG, "Network capabilities changed, refreshing state $networkCapabilities")
        refreshNetworkState()
    }

    override fun onLost(network: Network?) {
        Log.d(TAG, "Lost a network, refreshing state")
        refreshNetworkState()
    }

    private fun refreshNetworkState() {
        val connectedNetworks = connectivity.allNetworks.filter { connectivity.getNetworkInfo(it)?.isConnected == true }
        val wifiNetwork = connectedNetworks.find {
            val cap = connectivity.getNetworkCapabilities(it) ?: return@find false
            return@find cap.hasTransport(TRANSPORT_WIFI)
                    && cap.hasCapability(NET_CAPABILITY_INTERNET)
                    && cap.hasCapability(NET_CAPABILITY_VALIDATED)
        }
        val cellularNetwork = connectedNetworks.find {
            val cap = connectivity.getNetworkCapabilities(it) ?: return@find false
            return@find cap.hasTransport(TRANSPORT_CELLULAR)
                    && cap.hasCapability(NET_CAPABILITY_INTERNET)
                    && cap.hasCapability(NET_CAPABILITY_VALIDATED)
        }
        val newState = when {
            wifiNetwork != null -> {
                NetworkState(wifiConnected = true, wifiNetworkId = getWifiNetworkId(this.wifi))
            }
            cellularNetwork != null -> {
                NetworkState(cellularConnected = true)
            }
            else -> {
                NetworkState()
            }
        }

        if (newState != state.value) {
            CoroutineScope(Dispatchers.Main).launch {
                Log.i(TAG, "Network state changed: ${state.value} -> $newState")
                state.postValue(newState)
            }
        }
    }

}

fun getWifiNetworkId(manager: WifiManager): Int {
    if (manager.isWifiEnabled) {
        val wifiInfo = manager.connectionInfo
        if (wifiInfo != null) {
            val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
            if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                return wifiInfo.networkId
            }
        }
    }
    return 0
}