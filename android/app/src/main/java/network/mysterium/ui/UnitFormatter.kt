/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package network.mysterium.ui

import kotlin.math.roundToInt

object UnitFormatter {
    val KB = 1024
    val MB = 1024 * KB
    val GB = 1024 * MB

    fun bytesDisplay(bytes: Double): String {
        return when {
            bytes < KB -> "$bytes B"
            bytes < MB -> "%.2f KB".format(bytes / KB)
            bytes < GB -> "%.2f MB".format(bytes / MB)
            else -> "%.2f GB".format(bytes / GB)
        }
    }

    fun timeDisplay(seconds: Double): String {
        if (seconds < 0) {
            return "00:00:00"
        }

        val h = (seconds / 3600).roundToInt()
        val hh = when {
            h > 9 -> h.toString()
            else -> "0$h"
        }

        val m = ((seconds % 3600) / 60).roundToInt()
        val mm = when {
            m > 9 -> m.toString()
            else -> "0$m"
        }

        val s = (seconds % 60).roundToInt()
        val ss = when {
            s > 9 -> s.toString()
            else -> "0$s"
        }

        return "${hh}:${mm}:${ss}"
    }
}
