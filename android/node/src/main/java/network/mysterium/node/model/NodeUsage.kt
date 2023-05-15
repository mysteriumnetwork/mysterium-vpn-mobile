package network.mysterium.node.model

import kotlinx.serialization.Serializable

@Serializable
data class NodeUsage(
    val startTime: Long,
    val bytes: Long
)