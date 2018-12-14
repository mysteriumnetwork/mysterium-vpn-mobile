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
import android.widget.Toast
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

  private var mysteriumService: MysteriumCoreService?
    get() = _mysteriumService
    set(value) {
      _mysteriumService = value
      startIfReady()
    }
  private var _mysteriumService: MysteriumCoreService? = null

  private var vpnServiceGranted: Boolean
    get() = _vpnServiceGranted
    set(value) {
      _vpnServiceGranted = value
      startIfReady()
    }
  private var _vpnServiceGranted: Boolean = false

  private val serviceConnection = object : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
      Log.i(TAG, "Service disconnected")
      mysteriumService = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      Log.i(TAG, "Service connected")
      mysteriumService = service as MysteriumCoreService
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    bindMysteriumService()
    ensureVpnServicePermission()
  }

  override fun onDestroy() {
    unbindMysteriumService()
    super.onDestroy()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      VPN_SERVICE_REQUEST -> {
        if (resultCode != Activity.RESULT_OK) {
          Log.w(TAG, "User forbidden VPN service")
          showMessage("VPN connection has to be granted for MysteriumVPN to work.")
          finish()
          return
        }
        Log.i(TAG, "User allowed VPN service")
        vpnServiceGranted = true
      }
    }
  }

  private fun bindMysteriumService() {
    Log.i(TAG, "Binding service")
    Intent(this, MysteriumAndroidCoreService::class.java).also { intent ->
      bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
  }

  private fun unbindMysteriumService() {
    Log.i(TAG, "Unbinding service")
    unbindService(serviceConnection)
  }

  private fun ensureVpnServicePermission() {
    val intent: Intent? = VpnService.prepare(this)
    if (intent == null) {
      vpnServiceGranted = true
      return
    }
    startActivityForResult(intent, MainActivity.VPN_SERVICE_REQUEST)
  }

  private fun startIfReady() {
    val service = mysteriumService ?: return
    if (!vpnServiceGranted) {
      return
    }

    Log.i(TAG, "Starting node")
    try {
      service.StartTequila()
      Log.i(TAG, "Node started successfully")
    } catch (tr: Throwable) {
      Log.e(TAG, "Starting service failed", tr)
    }
  }

  private fun showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
  }

  companion object {
    private const val VPN_SERVICE_REQUEST = 1
    private const val TAG = "MainActivity"
  }
}
