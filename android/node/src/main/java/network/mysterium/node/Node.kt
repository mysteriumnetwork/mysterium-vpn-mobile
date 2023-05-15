package network.mysterium.node

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import network.mysterium.node.model.NodeConfig
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType
import network.mysterium.node.model.NodeTerms

interface Node {
    /**
     * Returns Node terms and conditions and terms of use content
     */
    val terms: NodeTerms

    /**
     * Returns url to NodeUI
     */
    val nodeUIUrl: String

    /**
     * Check if node is registered or not
     */
    val isRegistered: Boolean

    /**
     * Node config
     */
    val config: NodeConfig

    /**
     * Get list of current services and statuses.
     */
    val services: Flow<List<NodeServiceType>>

    /**
     * Get unsettled balance.
     */
    val balance: Flow<Double>

    /**
     * Get status of node.
     */
    val identity: StateFlow<NodeIdentity>

    /**
     * Get mobile limit reached status.
     */
    val limitMonitor: StateFlow<Boolean>

    /**
     * Update node config
     */
    suspend fun updateConfig(config: NodeConfig)

    /**
     * Initializes node and starts it in foreground service
     */
    suspend fun start()

    /**
     * Enable Android foreground service
     */
    suspend fun enableForegroundService()

    /**
     * Disable Android foreground service
     */
    fun disableForegroundService()

    /**
     * Stops node and foreground service
     */
    suspend fun stop()
}
