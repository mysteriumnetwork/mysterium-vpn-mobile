package network.mysterium.net

data class NetworkState(
        val wifiConnected: Boolean = false,
        val wifiNetworkId: Int = 0,
        val cellularConnected: Boolean = false
) {
    val connected: Boolean get() = wifiConnected or cellularConnected
}