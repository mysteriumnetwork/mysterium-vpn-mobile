package network.mysterium.net

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.*
import android.net.NetworkInfo.DetailedState.CONNECTED
import android.net.NetworkInfo.DetailedState.OBTAINING_IPADDR
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class NetworkMonitor(
        private val connectivity: ConnectivityManager,
        private val wifiManager: WifiManager,
        private val networkState: MutableLiveData<NetworkState>

) : ConnectivityManager.NetworkCallback() {

    companion object {
        private const val TAG = "NetworkMonitor"
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var activeNetwork: Network? = null
    private var delayedNetworkJoin: Job? = null
    private var delayedNetworkLoss: Job? = null
    private val networkStateChangeDelay = 7_000L

    fun start() {
        networkCallback = NetworkStatePublisher()
        connectivity.activeNetwork?.let {
            val capabilities = connectivity.getNetworkCapabilities(it)
            val initialState = when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> NetworkState(wifiConnected = true, wifiNetworkId = getWifiNetworkId())
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> NetworkState(cellularConnected = true)
                else -> NetworkState()
            }
            CoroutineScope(Dispatchers.Main).launch {
                networkState.value = initialState
            }
        }
        connectivity.registerDefaultNetworkCallback(networkCallback)
    }

    fun stop() {
        networkCallback?.let { connectivity.unregisterNetworkCallback(it) }
        networkCallback = null
    }

    inner class NetworkStatePublisher : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            if (network == null) {
                return
            }
            val networkInfo = connectivity.getNetworkInfo(network) ?: return
            val capabilities = connectivity.getNetworkCapabilities(network) ?: return

            val newState = when {
                capabilities.hasTransport(TRANSPORT_VPN) -> return
                capabilities.hasTransport(TRANSPORT_WIFI) -> NetworkState(wifiConnected = true, wifiNetworkId = getWifiNetworkId())
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> NetworkState(cellularConnected = true)
                else -> NetworkState()
            }

            if (activeNetwork != network) {
                delayedNetworkLoss?.let {
                    Log.i(TAG, "Network connectivity restored, canceling network loss")
                    it.cancel()
                    delayedNetworkLoss = null
                }
                delayedNetworkJoin?.let {
                    Log.i(TAG, "Connected to another network, canceling network join")
                    it.cancel()
                    delayedNetworkJoin = null
                }
                delayedNetworkJoin = CoroutineScope(Dispatchers.Main).launch {
                    delay(if (newState.wifiConnected) 500 else networkStateChangeDelay)
                    Log.i(TAG, "Network joined: ${transportType(network)} ${networkInfo.detailedState}")
                    networkState.value = newState
                    activeNetwork = network
                    delayedNetworkJoin = null
                }
            }
        }

        override fun onLost(network: Network?) {
            if (connectivity.activeNetworkInfo?.isConnected != true) {
                delayedNetworkLoss = CoroutineScope(Dispatchers.Main).launch {
                    delay(networkStateChangeDelay)
                    Log.i(TAG, "Network lost: ${transportType(network)}")
                    networkState.value = NetworkState()
                    delayedNetworkLoss = null
                }
            }
        }
    }

    private fun transportType(network: Network?): String {
        val cap = network?.let { connectivity.getNetworkCapabilities(it) } ?: return "UNKNOWN"
        return when {
            cap.hasTransport(TRANSPORT_WIFI) -> "WIFI"
            cap.hasTransport(TRANSPORT_CELLULAR) -> "CELLULAR"
            else -> "UNKNOWN"
        }
    }

    private fun getWifiNetworkId(): Int {
        if (wifiManager.isWifiEnabled) {
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo != null) {
                val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                if (state in listOf(CONNECTED, OBTAINING_IPADDR)) {
                    return wifiInfo.networkId
                }
            }
        }
        return 0
    }

}
