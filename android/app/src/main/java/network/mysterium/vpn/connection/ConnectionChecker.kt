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
