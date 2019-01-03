package network.mysterium.vpn.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log

/**
 * Starts ConnectionCheckerService periodically at a given interval.
 */
class ConnectionChecker(private val context: Context, private val interval: Long) {
  private var running: Boolean = false

  fun start() {
    Log.d(TAG, "Starting ConnectionChecker")
    running = true
    this.loopService()
  }

  fun stop() {
    Log.d(TAG, "Stopping ConnectionChecker")
    running = false
  }

  private fun loopService() {
    startService()
    Handler().postDelayed(this::loopService, interval)
  }

  private fun startService() {
    Log.d(TAG, "Starting ConnectionCheckerService")
    if (!this.running) {
      return
    }
    val service = Intent(context, ConnectionCheckerService::class.java)
    service.putExtras(Bundle())
    context.startService(service)
  }

  companion object {
    private const val TAG = "ConnectionChecker"
  }
}
