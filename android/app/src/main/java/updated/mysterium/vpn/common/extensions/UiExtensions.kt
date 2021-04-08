package updated.mysterium.vpn.common.extensions

import network.mysterium.proposal.NodeType

fun NodeType.getTypeLabel(): String = when(this) {
    NodeType.RESIDENTIAL -> "Residential"
    NodeType.CELLULAR -> "Residential"
    else -> "Non residential"
}
