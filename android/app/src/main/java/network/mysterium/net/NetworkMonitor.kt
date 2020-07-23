package network.mysterium.net

import android.net.*
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        Log.d(TAG, "onCapabilitiesChanged $network $networkCapabilities")
        val nc = networkCapabilities ?: return
        val connected = connectivity.getNetworkInfo(network).isConnected
        val newState = when {
            nc.hasTransport(TRANSPORT_CELLULAR) -> {
                NetworkState(cellularConnected = connected)
            }
            nc.hasTransport(TRANSPORT_WIFI) -> {
                NetworkState(wifiConnected = connected, wifiNetworkId = getWifiNetworkId(wifi))
            }
            else -> return
        }

        // Update state if conn was changed. It could change when switching from Wifi to Mobile network
        // or other different Wifi networks (in such case wifiConnId is used to check if wifi is changed).
        if (newState != state.value) {
            CoroutineScope(Dispatchers.Main).launch {
                state.value = newState
                Log.i(TAG, "Network state changed: ${state.value}")
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