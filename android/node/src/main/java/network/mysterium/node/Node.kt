package network.mysterium.node

import kotlinx.coroutines.flow.Flow
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
    var config: NodeConfig

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
    val identity: Flow<NodeIdentity>

    /**
     * Initializes node and starts it in foreground service
     */
    suspend fun start()

    /**
     * Start node services and enables foreground service to run node when app is closed.
     */
    fun startServices()

    /**
     * Stop services
     */
    fun stopServices()

    /**
     * Stops node and foreground service
     */
    suspend fun stop()
}
