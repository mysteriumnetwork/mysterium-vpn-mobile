package network.mysterium.node.model

import kotlinx.serialization.Serializable

@Serializable
data class NodeRunnerConfig(
    val useMobileData: Boolean = true,
    val useMobileDataLimit: Boolean = false,
    val mobileDataLimit: Int? = null
)
