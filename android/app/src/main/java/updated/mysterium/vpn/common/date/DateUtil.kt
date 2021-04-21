package updated.mysterium.vpn.common.date

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {

    private const val DEFAULT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val SHORT_PATTERN = "dd.MM"
    private const val MS_TO_SEC = 1000
    private const val SEC_TO_MN = 60
    private const val MN_TO_HOUR = 60
    private const val HOUR_TO_DAY = 24
    private const val DAY_IN_MS = MS_TO_SEC * SEC_TO_MN * MN_TO_HOUR * HOUR_TO_DAY
    private const val SEC_TYPE = " sec"
    private const val MIN_TYPE = " min"
    private const val HOUR_TYPE = " h"

    fun convertToDateType(dateMs: Long) = when {
        dateMs / MS_TO_SEC / SEC_TO_MN < 1 -> { // Time is less then a minute, show N sec
            (dateMs / MS_TO_SEC).toString() + SEC_TYPE
        }
        dateMs / MS_TO_SEC / SEC_TO_MN > 59 -> { // Time is more then an hour, show N h
            (dateMs / MS_TO_SEC / SEC_TO_MN / MN_TO_HOUR).toString() + HOUR_TYPE
        }
        else -> { // Time is less then an hour, show N min
            (dateMs / MS_TO_SEC / SEC_TO_MN).toString() + MIN_TYPE
        }
    }

    fun formatDate(date: String): String {
        val dateInMs = SimpleDateFormat(DEFAULT_PATTERN, Locale.ROOT).parse(date)?.time ?: 0L
        val dateFormat = SimpleDateFormat(SHORT_PATTERN, Locale.ROOT)
        return dateFormat.format(dateInMs)
    }

    fun dateDiffInDaysFromCurrent(date: String): Long {
        val dateInMs = SimpleDateFormat(DEFAULT_PATTERN, Locale.ROOT).parse(date)?.time ?: 0L
        // Convert milliseconds to days
        val currentDays = (Date().time / DAY_IN_MS)
        val dateDays = (dateInMs / DAY_IN_MS)
        return TimeUnit.DAYS.convert(currentDays - dateDays, TimeUnit.DAYS)
    }

    fun getHowLongHoursAgo(date: String): String {
        val dateFormat = SimpleDateFormat(DEFAULT_PATTERN, Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dateInMs = dateFormat.parse(date)?.time ?: 0L
        val msDiff = Date().time - dateInMs
        val hoursAgo = (msDiff / (MS_TO_SEC * SEC_TO_MN * MN_TO_HOUR))
        val minutesAgo = (msDiff % MN_TO_HOUR)
        return if (hoursAgo >= 1) {
            "${hoursAgo}h $minutesAgo min"
        } else {
            "$minutesAgo min"
        }
    }

    fun convertTimeToStringMinutesFormat(milliseconds: Long): String {
        val minutes = milliseconds / 1000 / 60
        val seconds = (milliseconds / 1000) - (minutes * 60)
        val formattedMinutes = if (minutes < 10) {
            "0$minutes"
        } else {
            minutes.toString()
        }
        val formattedSeconds = if (seconds < 10) {
            "0$seconds"
        } else {
            seconds.toString()
        }
        return formattedMinutes + formattedSeconds
    }
}
