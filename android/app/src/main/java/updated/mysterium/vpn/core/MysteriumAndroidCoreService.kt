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

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.vpn.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import updated.mysterium.vpn.analytics.AnalyticEvent
import updated.mysterium.vpn.analytics.mysterium.MysteriumAnalytic
import updated.mysterium.vpn.common.data.DisplayMoneyOptions
import updated.mysterium.vpn.common.data.PriceUtils
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.ConnectionStatistic
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.nodes.ProposalPaymentMoney
import updated.mysterium.vpn.model.proposal.parameters.ProposalViewItem
import updated.mysterium.vpn.model.statistics.Statistics
import updated.mysterium.vpn.model.statistics.StatisticsModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.notification.NotificationFactory
import updated.mysterium.vpn.notification.PushReceiver
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel

class MysteriumAndroidCoreService : VpnService(), KoinComponent {

    private companion object {
        const val TAG = "MysteriumVPNService"
        const val FIRST_BALANCE_LIMIT = 2.0
        const val SECOND_BALANCE_LIMIT = 1.0
        const val THIRD_BALANCE_LIMIT = 0.5
        const val MIN_BALANCE_LIMIT = THIRD_BALANCE_LIMIT * 0.2
        const val CURRENCY = "MYSTT"
    }

    private val appNotificationManager: AppNotificationManager by inject()
    private val useCaseProvider: UseCaseProvider by inject()
    private val analytic: MysteriumAnalytic by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private var mobileNode: MobileNode? = null
    private var activeProposal: ProposalViewItem? = null
    private var deferredNode: DeferredNode? = null
    private var isDisconnectManual = false
    private var currentState = ConnectionState.NOTCONNECTED
    private var vpnTimeSpent: Float? = null // time spent for last session in minutes
    private var secondsBetweenAnalyticEvent = 0
    private var isProviderActive = false

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

    private fun startMobileProviderService(active: Boolean) {
        mobileNode?.let {
            try {
                isProviderActive = active
                if (active) {
                    it.startProvider()
                } else {
                    it.stopProvider()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    private fun innerStopConsumer() {
        var c = currentState == ConnectionState.CONNECTED ||
                currentState == ConnectionState.CONNECTING ||
                currentState == ConnectionState.ON_HOLD ||
                currentState == ConnectionState.IP_NOT_CHANGED


        GlobalScope.launch(Dispatchers.IO) {
            if (c) {
                connectionUseCase.disconnect()

                activeProposal = null
                deferredNode = null
                stopForeground(true)
            }
        }
    }

    private fun startMobileNode(filesPath: String): MobileNode {
        mobileNode?.let {
            return it
        }
        mobileNode = Mysterium.newNode(filesPath, Mysterium.defaultNodeOptions())
        mobileNode?.overrideWireguardConnection(WireguardAndroidTunnelSetup(this@MysteriumAndroidCoreService))
        return mobileNode ?: MobileNode()
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
        GlobalScope.launch(Dispatchers.IO) {
            balanceUseCase.initBalanceListener {
                if (it < MIN_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isMinBalancePushShown()) {
                    makeBalancePushNotification()
                    balanceUseCase.minBalancePushShown()
                } else if (it < THIRD_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isThirdBalancePushShown()) {
                    makeBalancePushNotification()
                    balanceUseCase.thirdBalancePushShown()
                } else if (it < SECOND_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isSecondBalancePushShown()) {
                    makeBalancePushNotification()
                    balanceUseCase.secondBalancePushShown()
                } else if (it < FIRST_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isFirstBalancePushShown()) {
                    makeBalancePushNotification()
                    balanceUseCase.firstBalancePushShown()
                }
            }
        }
    }

    private fun initConnectionListener() {
        GlobalScope.launch(Dispatchers.IO) {
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
                            vpnTimeSpent = null
                        }
                        ConnectionState.CONNECTED -> {
                            isDisconnectManual = false
                            initStatisticListener()
                        }
                        ConnectionState.NOTCONNECTED -> {
                            connectionUseCase.clearDuration()
                            analytic.trackEvent(
                                eventName = AnalyticEvent.DISCONNECT_SUCCESS.eventName,
                                proposal = activeProposal?.let { proposal ->
                                    Proposal(proposal)
                                }
                            )
                            appNotificationManager.hideStatisticsNotification()
                            vpnTimeSpent = null
                            isDisconnectManual = false
                        }
                    }
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

    private fun initStatisticListener() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        GlobalScope.launch(handler) {
            val status = connectionUseCase.status()
            connectionUseCase.registerStatisticsChangeCallback {
                vpnTimeSpent = it.duration.toFloat() / 60
                val connectionModel = status.state
                if (connectionModel == ConnectionState.CONNECTED) {
                    updateStatistic(it)
                } else {
                    appNotificationManager.hideStatisticsNotification()
                }

                // Statistic updates every 5 sec.
                // BALANCE_UPDATE event sending every 60 sec.
                secondsBetweenAnalyticEvent += 5
                if (secondsBetweenAnalyticEvent >= 60) {
                    secondsBetweenAnalyticEvent = 0
                    analytic.trackEvent(
                        eventName = AnalyticEvent.BALANCE_UPDATE.eventName,
                        proposal = activeProposal?.let { proposal ->
                            Proposal(proposal)
                        }
                    )
                }
            }
        }
    }

    private fun updateStatistic(statisticsCallback: Statistics) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        GlobalScope.launch(handler) {
            connectionUseCase.setDuration(statisticsCallback.duration * 1000)
            val exchangeRate = exchangeRateViewModel.usdEquivalent
            val statistics = StatisticsModel.from(statisticsCallback)
            val connectionStatistic = ConnectionStatistic(
                duration = statistics.duration,
                bytesReceived = statistics.bytesReceived,
                bytesSent = statistics.bytesSent,
                tokensSpent = statistics.tokensSpent,
                currencySpent = exchangeRate * statistics.tokensSpent
            )
            val countryName = activeProposal?.countryName ?: "Unknown"
            val notificationTitle =
                getString(R.string.push_notification_connected_title, countryName)
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

        override fun stopConsumer() {
            innerStopConsumer()
        }
        override fun startProvider(active: Boolean) {
            startMobileProviderService(active)
        }
        override fun isProviderActive(): Boolean {
            return isProviderActive
        }

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
        }

        override fun getActiveProposal(): ProposalViewItem? {
            return activeProposal
        }

        override suspend fun startNode(): MobileNode {
            return startMobileNode(filesDir.canonicalPath)
        }

        override fun stopNode() {
            stopMobileNode()
        }

        override fun getContext(): Context {
            return this@MysteriumAndroidCoreService
        }

        override fun startForegroundWithNotification(
            id: Int,
            notificationFactory: NotificationFactory
        ) {
            startForeground(id, notificationFactory(this@MysteriumAndroidCoreService))
        }

        override fun stopForeground() {
            stopForeground(true)
        }
    }
}
