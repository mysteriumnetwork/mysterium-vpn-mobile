package updated.mysterium.vpn.model.manual.connect

import network.mysterium.ui.FormattedBytesViewItem

data class ConnectionStatistic(
    val duration: String,
    val bytesReceived: FormattedBytesViewItem,
    val bytesSent: FormattedBytesViewItem,
    val tokensSpent: Double,
    val currencySpent: Double
)
