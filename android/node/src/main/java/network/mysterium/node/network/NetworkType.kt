package network.mysterium.node.network

import android.net.NetworkCapabilities

enum class NetworkType {
    WIFI,
    MOBILE;

    val capability: Int
        get() = when (this) {
            WIFI -> NetworkCapabilities.TRANSPORT_WIFI
            MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
        }
}