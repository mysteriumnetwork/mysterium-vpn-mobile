package network.mysterium.node.network

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.concurrent.TimeUnit


class NetworkReporter(
    context: Context
) {

    val currentConnectivity: StateFlow<List<NetworkType>>
        get() = currentConnectivityFlow.asStateFlow()

    private val currentConnectivityFlow = MutableStateFlow<List<NetworkType>>(emptyList())
    private val connectivity =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkStatsManager =
        context.getSystemService(NetworkStatsManager::class.java)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            currentConnectivityFlow.update {
                currentNetworkTypes()
            }
        }
    }


    init {
        connectivity.registerDefaultNetworkCallback(networkCallback)
    }

    fun isConnected(type: NetworkType): Boolean {
        val network = connectivity.activeNetwork ?: return false
        val activeNetwork = connectivity.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(type.capability)
    }

    fun isOnline(): Boolean {
        return isConnected(NetworkType.MOBILE) || isConnected(NetworkType.WIFI)
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun monitorUsage(type: NetworkType) = callbackFlow {
        val networkType = type.capability
        val interval = TimeUnit.SECONDS.toMillis(10)
        var isRunning = true
        var startTime = Date().time - interval
        var endTime: Long

        while (isRunning) {
            Log.d("NetworkReporter", "Start: $startTime; End: ${Date().time}")
            endTime = Date().time
            val response = networkStatsManager.querySummary(
                networkType,
                null,
                startTime,
                endTime
            )
            startTime = endTime

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

    private fun currentNetworkTypes(): List<NetworkType> {
        return NetworkType.values().mapNotNull {
            if (isConnected(it)) {
                it
            } else {
                null
            }
        }

    }
}
