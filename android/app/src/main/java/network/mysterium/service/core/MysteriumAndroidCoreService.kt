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

package network.mysterium.service.core

import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.IBinder
import android.util.Log
import mysterium.MobileNode
import mysterium.Mysterium

class MysteriumAndroidCoreService : VpnService() {
  private var mobileNode: MobileNode? = null

  fun startMobileNode(filesPath: String) {
    val openvpnBridge = Openvpn3AndroidTunnelSetupBridge(this)
    val wireguardBridge = WireguardAndroidTunnelSetup(this)
    val options = Mysterium.defaultNetworkOptions()
    options.experimentNATPunching = true

    mobileNode = Mysterium.newNode(filesPath, options)
    mobileNode?.overrideOpenvpnConnection(openvpnBridge)
    mobileNode?.overrideWireguardConnection(wireguardBridge)
    Log.i(TAG, "started")
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
