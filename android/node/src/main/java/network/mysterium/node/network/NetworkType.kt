package network.mysterium.node.network

import android.net.NetworkCapabilities

enum class NetworkType {
    WIFI,
    MOBILE,
    NOT_CONNECTED;

    val capability: Int
        get() = when (this) {
            WIFI -> NetworkCapabilities.TRANSPORT_WIFI
            MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
            else -> -1
        }
}
