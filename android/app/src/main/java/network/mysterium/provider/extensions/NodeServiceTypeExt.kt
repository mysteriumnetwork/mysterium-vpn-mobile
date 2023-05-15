package network.mysterium.provider.extensions

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import network.mysterium.node.model.NodeServiceType
import network.mysterium.provider.R
import network.mysterium.provider.ui.theme.Colors

val NodeServiceType.State.bgColor: Color
    get() = when (this) {
        NodeServiceType.State.UNKNOWN,
        NodeServiceType.State.NOT_RUNNING,
        NodeServiceType.State.STARTING -> {
            Colors.serviceNotRunningBg
        }
        NodeServiceType.State.RUNNING -> {
            Colors.serviceRunningBg
        }
    }

val NodeServiceType.State.textColor: Color
    get() = when (this) {
        NodeServiceType.State.UNKNOWN,
        NodeServiceType.State.NOT_RUNNING,
        NodeServiceType.State.STARTING -> {
            Colors.textDisabled
        }
        NodeServiceType.State.RUNNING -> {
            Colors.textPrimary
        }
    }

val NodeServiceType.State.dotColor: Color
    get() = when (this) {
        NodeServiceType.State.UNKNOWN,
        NodeServiceType.State.NOT_RUNNING,
        NodeServiceType.State.STARTING -> {
            Colors.serviceNotRunningDot
        }
        NodeServiceType.State.RUNNING -> {
            Colors.serviceRunningDot
        }
    }


val NodeServiceType.Service.nameResId: Int
    get() = when (this) {
        NodeServiceType.Service.WIREGUARD -> R.string.service_public
        NodeServiceType.Service.SCRAPING -> R.string.service_data_scraping
        NodeServiceType.Service.DATA_TRANSFER -> R.string.service_data_transfer
        NodeServiceType.Service.OTHER -> R.string.service_other
    }