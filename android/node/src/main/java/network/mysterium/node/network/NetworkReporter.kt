package network.mysterium.node.network

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import network.mysterium.node.date.DateUtil
import java.util.Date
import java.util.concurrent.TimeUnit

class NetworkReporter(
    context: Context
) {

    val currentConnectivity: StateFlow<NetworkType>
        get() = currentConnectivityFlow.asStateFlow()

    private val currentConnectivityFlow = MutableStateFlow<NetworkType>(NetworkType.NOT_CONNECTED)
    private val connectivity =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkStatsManager =
        context.getSystemService(NetworkStatsManager::class.java)

    private val availableCellularNetworkSet = HashSet<Long>()
    private val availableWifiNetworkSet = HashSet<Long>()

    init {
        addNetworkStateListener(
            connectivity,
            NetworkCapabilities.TRANSPORT_WIFI,
            availableWifiNetworkSet
        )
        addNetworkStateListener(
            connectivity,
            NetworkCapabilities.TRANSPORT_CELLULAR,
            availableCellularNetworkSet
        )
    }

    private fun addNetworkStateListener(
        connectivityManager: ConnectivityManager,
        transport: Int,
        networkSetToUpdate: HashSet<Long>
    ) {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(transport)
            .build()

        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    networkSetToUpdate.add(network.networkHandle)
                    updateConnectionAvailableState()
                }

                override fun onLost(network: Network) {
                    networkSetToUpdate.remove(network.networkHandle)
                    updateConnectionAvailableState()
                }
            }
        )
    }

    private fun updateConnectionAvailableState() {
        currentConnectivityFlow.value = when {
            availableWifiNetworkSet.size > 0 -> NetworkType.WIFI
            availableCellularNetworkSet.size > 0 -> NetworkType.MOBILE
            else -> NetworkType.NOT_CONNECTED
        }
    }

    fun isConnected(type: NetworkType): Boolean = currentConnectivityFlow.value == type

    fun isOnline(): Boolean {
        return currentConnectivityFlow.value != NetworkType.NOT_CONNECTED
    }

    fun monitorUsage(type: NetworkType) = callbackFlow {
        val networkType = type.capability
        val interval = TimeUnit.SECONDS.toMillis(5)
        var isRunning = true
        val startTime = DateUtil.getMillisecondsOfFirstDayOfMonth()

        while (isRunning) {
            val response = networkStatsManager.querySummary(
                networkType,
                null,
                startTime,
                Date().time,
            )

            var usage = 0L
            while (true) {
                val bucket = NetworkStats.Bucket()
                response.getNextBucket(bucket)
                usage += bucket.rxBytes + bucket.txBytes
                if (!response.hasNextBucket()) {
                    break
                }
            }

            send(usage)
            delay(interval)
        }

        awaitClose {
            isRunning = false
        }
    }
}
