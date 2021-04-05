package updated.mysterium.vpn.common.data

import mysterium.Estimates
import network.mysterium.ui.UnitFormatter

object WalletEstimatesUtil {

    private const val MIN = "min"
    private const val HOUR = "h"

    fun convertVideoData(estimates: Estimates) = if (estimates.videoMinutes < 60) {
        estimates.videoMinutes.toString()
    } else {
        (estimates.videoMinutes / 60).toString()
    }

    fun convertVideoType(estimates: Estimates) = if (estimates.videoMinutes < 60) {
        MIN
    } else {
        HOUR
    }

    fun convertWebData(estimates: Estimates) = if (estimates.browsingMinutes < 60) {
        estimates.browsingMinutes.toString()
    } else {
        (estimates.browsingMinutes / 60).toString()
    }

    fun convertWebType(estimates: Estimates) = if (estimates.browsingMinutes < 60) {
        MIN
    } else {
        HOUR
    }

    fun convertDownloadData(estimates: Estimates): String {
        val downloadData = UnitFormatter.bytesDisplay(estimates.trafficMB * 1024 * 1024)
        return downloadData.value
            .replace(',', '.')
            .toDouble()
            .toLong()
            .toString()
    }

    fun convertDownloadType(estimates: Estimates): String {
        val downloadData = UnitFormatter.bytesDisplay(estimates.trafficMB * 1024 * 1024)
        return downloadData.units
    }

    // One song - 3 minute.toString()
    fun convertMusicCount(estimates: Estimates) = (estimates.musicMinutes / 3L).toString()

    fun convertMusicTimeData(estimates: Estimates) = if (estimates.musicMinutes < 60) {
        estimates.musicMinutes.toString()
    } else {
        (estimates.musicMinutes / 60).toString()
    }

    fun convertMusicTimeType(estimates: Estimates) = if (estimates.musicMinutes < 60) {
        MIN
    } else {
        HOUR
    }
}
