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
  fun start() {
    Log.d(TAG, "Starting ConnectionChecker")
    this.loopService()
  }

  private fun loopService() {
    startService()
    Handler().postDelayed(this::loopService, interval)
  }

  private fun startService() {
    Log.d(TAG, "Starting ConnectionCheckerService")
    val service = Intent(context, ConnectionCheckerService::class.java)
    service.putExtras(Bundle())
    context.startService(service)
  }

  companion object {
    private const val TAG = "ConnectionChecker"
  }
}
