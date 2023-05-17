package network.mysterium.node.network

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.concurrent.TimeUnit


internal class NetworkReporter(
    context: Context
) {

    val currentConnectivity: StateFlow<List<NetworkType>>
        get() = currentConnectivityFlow.asStateFlow()

    private val currentConnectivityFlow = MutableStateFlow<List<NetworkType>>(emptyList())
    private val connectivity =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkStatsManager =
        context.getSystemService(NetworkStatsManager::class.java)
    private val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

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

    @SuppressLint("MissingPermission", "HardwareIds")
    fun monitorUsage(type: NetworkType) = callbackFlow {
        val networkType = type.capability
        val subscribeId: String? = when (type) {
            NetworkType.WIFI -> null
            NetworkType.MOBILE -> getSubscriberId()
        }

        val interval = TimeUnit.SECONDS.toMillis(10)
        var isRunning = true
        while (isRunning) {
            val response = networkStatsManager.querySummary(
                networkType, subscribeId,
                Date().time - interval,
                Date().time
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

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSubscriberId(): String? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            telephony.subscriberId
        } else {
            null
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
