package updated.mysterium.vpn.model.manual.connect

import updated.mysterium.vpn.common.data.FormattedBytesViewItem

data class ConnectionStatistic(
    val duration: String,
    val bytesReceived: FormattedBytesViewItem,
    val bytesSent: FormattedBytesViewItem,
    val tokensSpent: Double,
    val currencySpent: Double
)
