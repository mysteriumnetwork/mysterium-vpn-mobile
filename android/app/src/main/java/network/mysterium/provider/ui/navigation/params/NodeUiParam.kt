package network.mysterium.provider.ui.navigation.params

import kotlinx.serialization.Serializable
import network.mysterium.provider.DeeplinkPath

@Serializable
data class NodeUiParam(val scheme: DeeplinkPath.Scheme, val parameter: String?)
