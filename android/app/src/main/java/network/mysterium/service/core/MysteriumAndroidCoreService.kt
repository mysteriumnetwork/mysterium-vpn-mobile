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

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.IBinder
import android.util.Log
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.NotificationFactory
import network.mysterium.proposal.ProposalViewItem

class MysteriumAndroidCoreService : VpnService() {
    private var mobileNode: MobileNode? = null

    private var activeProposal: ProposalViewItem? = null

    override fun onDestroy() {
        super.onDestroy()
        stopMobileNode()
    }

    override fun onRevoke() {
        Log.w(TAG, "VPN service revoked!")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return MysteriumCoreServiceBridge()
    }

    private fun startMobileNode(filesPath: String): MobileNode {
        if (mobileNode != null) {
            return mobileNode!!
        }

        val wireguardBridge = WireguardAndroidTunnelSetup(this)

        val options = Mysterium.defaultNodeOptions()

        mobileNode = Mysterium.newNode(filesPath, options)
        mobileNode?.overrideWireguardConnection(wireguardBridge)

        Log.i(TAG, "Node started")
        return mobileNode!!
    }

    private fun stopMobileNode() {
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
        } finally {
            stopForeground(true)
        }
    }

    inner class MysteriumCoreServiceBridge : Binder(), MysteriumCoreService {
        override fun setActiveProposal(proposal: ProposalViewItem?) {
            activeProposal = proposal
        }

        override fun getActiveProposal(): ProposalViewItem? {
            return activeProposal
        }

        override fun startNode(): MobileNode {
            return startMobileNode(filesDir.canonicalPath)
        }

        override fun stopNode() {
            stopMobileNode()
        }

        override fun getContext(): Context {
            return this@MysteriumAndroidCoreService
        }

        override fun startForegroundWithNotification(id: Int, notificationFactory: NotificationFactory) {
            startForeground(id, notificationFactory(this@MysteriumAndroidCoreService))
        }

        override fun stopForeground() {
            stopForeground(true)
        }
    }

    companion object {
        private const val TAG = "MysteriumVPNService"
    }
}
