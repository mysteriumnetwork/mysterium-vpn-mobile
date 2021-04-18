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
import android.content.IntentFilter
import android.net.VpnService
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.NotificationFactory
import network.mysterium.notification.PushReceiver
import network.mysterium.proposal.ProposalViewItem
import network.mysterium.vpn.R
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.*

class MysteriumAndroidCoreService : VpnService(), KoinComponent {

    private companion object {
        const val TAG = "MysteriumVPNService"
        const val BALANCE_LIMIT = 1.0
        const val MIN_BALANCE_LIMIT = BALANCE_LIMIT * 0.1
    }

    private val useCaseProvider: UseCaseProvider by inject()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private var mobileNode: MobileNode? = null
    private var activeProposal: ProposalViewItem? = null
    private var deferredNode: DeferredNode? = null
    private var isDisconnectManual = false

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
        options.pilvytisAddress = "http://hadex.lt:8002/api/v1" // Testing payment, should be deleted after testing
        mobileNode = Mysterium.newNode(filesPath, options)
        mobileNode?.overrideWireguardConnection(wireguardBridge)

        Log.i(TAG, "Node started")
        initBalanceListener()
        initConnectionListener()
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

    private fun initBalanceListener() {
        GlobalScope.launch {
            balanceUseCase.initBalanceListener {
                if (it < BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isBalancePushShown()) {
                    makeBalancePushNotification()
                    balanceUseCase.balancePushShown()
                }
                if (it < MIN_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isMinBalancePushShown()) {
                    makeBalancePushNotification()
                    balanceUseCase.minBalancePushShown()
                }
            }
        }
    }

    private fun initConnectionListener() {
        GlobalScope.launch {
            connectionUseCase.connectionStatusCallback {
                val connectionStateModel = ConnectionState.valueOf(it.toUpperCase(Locale.ROOT))
                if (connectionStateModel == ConnectionState.DISCONNECTING && !isDisconnectManual) {
                    makeConnectionPushNotification()
                } else if (
                    connectionStateModel == ConnectionState.CONNECTED ||
                    connectionStateModel == ConnectionState.NOTCONNECTED
                ) {
                    isDisconnectManual = false
                }
            }
        }
    }

    private fun makeConnectionPushNotification() {
        registerReceiver(PushReceiver(), IntentFilter(PushReceiver.PUSHY_CONNECTION_ACTION))
        val extra = Bundle().apply {
            putString(
                PushReceiver.NOTIFICATION_TITLE,
                getString(R.string.push_notification_connection_title)
            )
            putString(
                PushReceiver.NOTIFICATION_MESSAGE,
                getString(R.string.push_notification_connection_message)
            )
        }
        sendBroadcast(Intent(PushReceiver.PUSHY_CONNECTION_ACTION).putExtras(extra))
    }

    private fun makeBalancePushNotification() {
        registerReceiver(PushReceiver(), IntentFilter(PushReceiver.PUSHY_BALANCE_ACTION))
        val extra = Bundle().apply {
            putString(
                PushReceiver.NOTIFICATION_TITLE,
                getString(R.string.push_notification_balance_title)
            )
            putString(
                PushReceiver.NOTIFICATION_MESSAGE,
                getString(R.string.push_notification_balance_message)
            )
        }
        sendBroadcast(Intent(PushReceiver.PUSHY_BALANCE_ACTION).putExtras(extra))
    }

    inner class MysteriumCoreServiceBridge : Binder(), MysteriumCoreService {

        override fun getDeferredNode() = deferredNode

        override fun setDeferredNode(node: DeferredNode?) {
            deferredNode = node
        }

        override fun subscribeToListeners() {
            initBalanceListener()
            initConnectionListener()
        }

        override fun manualDisconnect() {
            isDisconnectManual = true
        }

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
}
