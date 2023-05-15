package network.mysterium.node.extensions

import java.util.Calendar
import java.util.Date

fun Calendar.nextDay(): Date {
    val year = this[Calendar.YEAR]
    val month = this[Calendar.MONTH]
    val day = this[Calendar.DATE]
    timeInMillis = 0
    this[year, month, day + 1, 0, 0] = 0
    return time
}

val Calendar.isFirstDayOfMonth: Boolean
    get() = this[Calendar.DAY_OF_MONTH] == 1