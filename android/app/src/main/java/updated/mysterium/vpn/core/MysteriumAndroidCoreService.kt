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

package updated.mysterium.vpn.core

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.vpn.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import updated.mysterium.vpn.analitics.AnalyticEvent
import updated.mysterium.vpn.analitics.AnalyticWrapper
import updated.mysterium.vpn.common.data.DisplayMoneyOptions
import updated.mysterium.vpn.common.data.PriceUtils
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.ConnectionStatistic
import updated.mysterium.vpn.model.nodes.ProposalPaymentMoney
import updated.mysterium.vpn.model.proposal.parameters.ProposalViewItem
import updated.mysterium.vpn.model.statistics.Statistics
import updated.mysterium.vpn.model.statistics.StatisticsModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.notification.NotificationFactory
import updated.mysterium.vpn.notification.PushReceiver
import java.util.*

class MysteriumAndroidCoreService : VpnService(), KoinComponent {

    private companion object {
        const val TAG = "MysteriumVPNService"
        const val BALANCE_LIMIT = 0.5
        const val MIN_BALANCE_LIMIT = BALANCE_LIMIT * 0.2
        const val CURRENCY = "MYSTT"
    }

    private lateinit var appNotificationManager: AppNotificationManager
    private val useCaseProvider: UseCaseProvider by inject()
    private val analyticWrapper: AnalyticWrapper by inject()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private var mobileNode: MobileNode? = null
    private var activeProposal: ProposalViewItem? = null
    private var deferredNode: DeferredNode? = null
    private var isDisconnectManual = false
    private var currentState = ConnectionState.NOTCONNECTED
    private var vpnTimeSpent: Float? = null // time spent for last session in minutes

    override fun onDestroy() {
        stopMobileNode()
        super.onDestroy()
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
        // Testing payment, should be deleted after testing
        mobileNode = Mysterium.newNode(filesPath, options)
        mobileNode?.overrideWireguardConnection(wireguardBridge)

        Log.i(TAG, "Node started")
        initBalanceListener()
        initConnectionListener()
        appNotificationManager = AppNotificationManager(
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        ).apply { init(this@MysteriumAndroidCoreService) }
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
                val state = ConnectionState.from(it)
                val previousState = currentState
                currentState = state
                if (previousState != currentState) {
                    when (currentState) {
                        ConnectionState.DISCONNECTING -> {
                            if (!isDisconnectManual) {
                                makeConnectionPushNotification()
                            }
                            vpnTimeSpent?.let { time ->
                                analyticWrapper.track(AnalyticEvent.VPN_TIME, time)
                                vpnTimeSpent = null
                            }
                        }
                        ConnectionState.CONNECTED -> {
                            analyticWrapper.track(AnalyticEvent.NEW_SESSION)
                            isDisconnectManual = false
                            initStatisticListener()
                        }
                        ConnectionState.NOTCONNECTED -> {
                            appNotificationManager.hideStatisticsNotification()
                            vpnTimeSpent = null
                            isDisconnectManual = false
                        }
                    }
                }
            }
        }
    }

    private fun trackConnectedCountry(countryName: String) {
        analyticWrapper.track(AnalyticEvent.COUNTRY_SELECTED, countryName)
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

    private fun initStatisticListener() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        GlobalScope.launch(handler) {
            val status = connectionUseCase.status()
            connectionUseCase.registerStatisticsChangeCallback {
                vpnTimeSpent = it.duration.toFloat() / 60
                val connectionModel = ConnectionState.from(status.state)
                if (connectionModel == ConnectionState.CONNECTED) {
                    updateStatistic(it)
                } else {
                    appNotificationManager.hideStatisticsNotification()
                }
            }
        }
    }

    private fun updateStatistic(statisticsCallback: Statistics) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        GlobalScope.launch(handler) {
            val exchangeRate = balanceUseCase.getUsdEquivalent()
            val statistics = StatisticsModel.from(statisticsCallback)
            val connectionStatistic = ConnectionStatistic(
                duration = statistics.duration,
                bytesReceived = statistics.bytesReceived,
                bytesSent = statistics.bytesSent,
                tokensSpent = statistics.tokensSpent,
                currencySpent = exchangeRate * statistics.tokensSpent
            )
            val countryName = activeProposal?.countryName ?: "Unknown"
            val notificationTitle = getString(R.string.push_notification_connected_title, countryName)
            val tokensSpent = PriceUtils.displayMoney(
                ProposalPaymentMoney(
                    amount = connectionStatistic.tokensSpent,
                    currency = CURRENCY
                ),
                DisplayMoneyOptions(fractionDigits = 3, showCurrency = true)
            )
            val notificationContent = getString(
                R.string.push_notification_content,
                "${connectionStatistic.bytesReceived.value} ${connectionStatistic.bytesReceived.units}",
                "${connectionStatistic.bytesSent.value} ${connectionStatistic.bytesSent.units}",
                tokensSpent
            )
            appNotificationManager.showStatisticsNotification(
                notificationTitle, notificationContent
            )
        }
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
            appNotificationManager.hideStatisticsNotification()
        }

        override fun setActiveProposal(proposal: ProposalViewItem?) {
            activeProposal = proposal
            activeProposal?.let {
                trackConnectedCountry(it.countryName)
            }
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
