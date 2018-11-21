package network.mysterium.vpn

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.facebook.react.ReactActivity
import network.mysterium.service.core.MysteriumAndroidCoreService
import network.mysterium.service.core.MysteriumCoreService

class MainActivity : ReactActivity() {
  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   */
  override fun getMainComponentName(): String? {
    return "MysteriumVPN"
  }

  var mysteriumService: MysteriumCoreService? = null

  private val serviceConnection = object : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
      Log.i(TAG, "Service disconnected")
      mysteriumService = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      Log.i(TAG, "Service connected")
      mysteriumService = service as MysteriumCoreService
      startTequila()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.i(TAG, "Binding service")

    Intent(this, MysteriumAndroidCoreService::class.java).also { intent ->
      bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
  }

  override fun onDestroy() {
    Log.i(TAG, "Unbinding service")
    unbindService(serviceConnection)
    super.onDestroy()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      VPN_SERVICE_REQUEST -> {
        Log.i(TAG, "User allowed VPN service? " + (resultCode == Activity.RESULT_OK))
      }
    }
  }

  private fun startTequila() {
    Log.i(TAG, "Will start node")
    try {
      startService(this)
    } catch (tr: Throwable) {
      Log.e(TAG, "Starting service failed:", tr)
      tr.printStackTrace()
    }
    Log.i(TAG, "Node started successfully")
  }

  private fun startService(activity: MainActivity) {
    val intent = VpnService.prepare(activity)
    if (intent != null) {
      activity.startActivityForResult(intent, MainActivity.VPN_SERVICE_REQUEST)
      return
    }

    val service = activity.mysteriumService
        ?: throw Error("Mysterium service is not set on activity")
    service.StartTequila()
  }

  companion object {
    const val VPN_SERVICE_REQUEST = 1
  }
}

private const val TAG = "App"
