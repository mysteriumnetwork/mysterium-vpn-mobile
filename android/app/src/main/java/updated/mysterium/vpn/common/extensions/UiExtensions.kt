package updated.mysterium.vpn.common.extensions

import androidx.annotation.StringRes
import network.mysterium.proposal.NodeType
import network.mysterium.vpn.R

@StringRes
fun NodeType.getTypeLabelResource(): Int = when (this) {
    NodeType.RESIDENTIAL -> R.string.manual_connect_residential
    NodeType.CELLULAR -> R.string.manual_connect_residential
    else -> R.string.manual_connect_non_residential
}
