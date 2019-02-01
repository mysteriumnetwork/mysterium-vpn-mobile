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

package network.mysterium.vpn.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.lang.IllegalStateException

/**
 * Starts ConnectionCheckerService periodically at a given interval.
 */
class ConnectionChecker(private val context: Context, private val interval: Long) {
  private var running: Boolean = false

  fun start() {
    if (running) {
      return
    }

    Log.d(TAG, "Starting ConnectionChecker")
    running = true
    this.loopService(ignoreLastStatus = true)
  }

  fun stop() {
    if (!running) {
      return
    }

    Log.d(TAG, "Stopping ConnectionChecker")
    running = false
  }

  private fun loopService(ignoreLastStatus: Boolean) {
    if (!running) {
      return
    }
    startService(ignoreLastStatus)

    Handler().postDelayed({ this.loopService(ignoreLastStatus = false) }, interval)
  }

  private fun startService(ignoreLastStatus: Boolean) {
    Log.d(TAG, "Starting ConnectionCheckerService")
    val service = Intent(context, ConnectionCheckerService::class.java)
    val bundle = Bundle()
    bundle.putBoolean("ignoreLastStatus", ignoreLastStatus)
    service.putExtras(bundle)
    try {
      context.startService(service)
    } catch (e: IllegalStateException) {
      // We stop checker, because app is in a state where the service can not be started.
      // This is usually because app was in the background for too long.
      stop()
    }
  }

  companion object {
    private const val TAG = "ConnectionChecker"
  }
}
