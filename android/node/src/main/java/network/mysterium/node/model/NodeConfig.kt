package network.mysterium.node.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NodeConfig(
    val useMobileData: Boolean = true,
    val useMobileDataLimit: Boolean = false,
    val mobileDataLimit: Long? = null,
    val allowUseOnBattery: Boolean = true,
    @SerialName("android_sso_deeplink")
    val androidSsoDeeplink: Boolean = true,
)
