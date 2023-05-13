package network.mysterium.node

import kotlinx.coroutines.flow.Flow
import network.mysterium.node.model.NodeRunnerConfig
import network.mysterium.node.model.NodeRunnerService
import network.mysterium.node.model.NodeRunnerTerms

interface Node {
    /**
     * Returns Node terms and conditions and terms of use content
     */
    val terms: NodeRunnerTerms

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
    var config: NodeRunnerConfig

    /**
     * Initializes node and starts it in foreground service
     */
    suspend fun start()

    /**
     * Enable foreground service to run node when app is closed.
     */
    fun enableForeground()

    /**
     * Stops node and foreground service
     */
    suspend fun stop()

    /**
     * Get list of current services and statuses.
     */
    suspend fun getServices(): Flow<List<NodeRunnerService>>
}
