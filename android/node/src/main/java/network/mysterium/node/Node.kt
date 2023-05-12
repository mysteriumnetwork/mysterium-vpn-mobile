package network.mysterium.node

import kotlinx.coroutines.flow.Flow
import network.mysterium.node.model.NodeService
import network.mysterium.node.model.NodeTerms

interface Node {
    val terms: NodeTerms
    val nodeUIUrl: String
    suspend fun initialize()
    suspend fun getServices(): Flow<List<NodeService>>
}
