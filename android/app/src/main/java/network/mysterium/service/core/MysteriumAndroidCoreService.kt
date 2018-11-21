package network.mysterium.service.core

import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.IBinder
import android.util.Log
import mysterium.MobileNetworkOptions
import mysterium.MobileNode
import mysterium.Mysterium

class MysteriumAndroidCoreService : VpnService() {
  private var mobileNode: MobileNode? = null

  fun startMobileNode(filesPath: String) {
    val androidOpenvpnBridge = Openvpn3AndroidTunnelSetupBridge(this)
    var options = Mysterium.defaultNetworkOptions()
    options.experimentIdentityCheck = true
    options.experimentPromiseCheck = true

    options = getLocalnetOptions(options)

    mobileNode = Mysterium.newNode(filesPath, options, androidOpenvpnBridge)
    Log.i(TAG, "started.")
  }

  fun stopMobileNode() {
    val node = mobileNode
    if (node == null) {
      Log.w(TAG, "Trying to stop node when instance is not set")
      return
    }

    node.shutdown()
    try {
      node.waitUntilDies()
    } catch (e: Exception) {
      Log.i(TAG, "Got exception, safe to ignore: " + e.message)
    }
  }

  private fun getLocalnetOptions(options: MobileNetworkOptions): MobileNetworkOptions {
    options.discoveryAPIAddress = "http://192.168.1.62/v1"
    options.brokerAddress = "192.168.1.62"
    options.etherClientRPC = "http://192.168.1.62:8545"
    options.testnet = false
    options.localnet = true
    options.etherPaymentsAddress = "0x1955141ba8e77a5B56efBa8522034352c94f77Ea"

    return options
  }

  override fun onRevoke() {
    Log.w(TAG, "VPN service revoked!")
  }

  inner class MysteriumCoreServiceBridge : Binder(), MysteriumCoreService {
    override fun StartTequila() {
      startMobileNode(filesDir.canonicalPath)
    }

    override fun StopTequila() {
      stopMobileNode()
    }
  }

  override fun onBind(intent: Intent?): IBinder? {
    return MysteriumCoreServiceBridge()
  }

  companion object {
    private const val TAG = "Mysterium vpn service"
  }
}
