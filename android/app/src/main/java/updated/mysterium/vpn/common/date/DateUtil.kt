package updated.mysterium.vpn.common.date

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private const val DEFAULT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val MS_TO_SEC = 1000
    private const val SEC_TO_MN = 60
    private const val MN_TO_HOUR = 60
    private const val SEC_TYPE = " sec"
    private const val MIN_TYPE = " min"
    private const val HOUR_TYPE = " h"

    fun dateDiff(firstDate: String, secondDate: String, pattern: String = DEFAULT_PATTERN): Long {
        val firstDateMs = SimpleDateFormat(pattern, Locale.ROOT).parse(firstDate)?.time ?: 0L
        val secondDateMS = SimpleDateFormat(pattern, Locale.ROOT).parse(secondDate)?.time ?: 0L
        return secondDateMS - firstDateMs
    }

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
}
