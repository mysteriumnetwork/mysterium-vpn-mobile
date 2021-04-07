package updated.mysterium.vpn.common.connection

import android.net.NetworkCapabilities

object ConnectionUtil {

    fun isConnectionWithoutVPNAvailable(
        activeNetworksCapabilities: NetworkCapabilities
    ) = when {
        activeNetworksCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetworksCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetworksCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }

    fun isConnectionWithVPNAvailable(
        activeNetworksCapabilities: NetworkCapabilities
    ) = activeNetworksCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        activeNetworksCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
