package updated.mysterium.vpn.model.statistics

import updated.mysterium.vpn.common.data.FormattedBytesViewItem
import updated.mysterium.vpn.common.data.UnitFormatter

class StatisticsModel(
    val duration: String,
    val bytesReceived: FormattedBytesViewItem,
    val bytesSent: FormattedBytesViewItem,
    val tokensSpent: Double
) {
    companion object {
        fun from(stats: Statistics): StatisticsModel {
            return StatisticsModel(
                duration = UnitFormatter.timeDisplay(stats.duration),
                bytesReceived = UnitFormatter.bytesDisplay(stats.bytesReceived),
                bytesSent = UnitFormatter.bytesDisplay(stats.bytesSent),
                tokensSpent = stats.tokensSpent
            )
        }
    }
}
