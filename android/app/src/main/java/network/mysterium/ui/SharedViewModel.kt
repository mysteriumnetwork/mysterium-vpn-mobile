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

package network.mysterium.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mysterium.ConnectRequest
import network.mysterium.AppNotificationManager
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.NodeRepository
import network.mysterium.service.core.Statistics
import network.mysterium.service.core.Status

enum class ConnectionState(val type: String) {
    UNKNOWN("Unknown"),
    CONNECTED("Connected"),
    CONNECTING("Connecting"),
    NOT_CONNECTED("NotConnected"),
    DISCONNECTING("Disconnecting");

    companion object {
        fun parse(type: String): ConnectionState {
            return values().find { it.type == type } ?: UNKNOWN
        }
    }
}

class LocationModel(
        val ip: String,
        val countryFlagImage: Bitmap?
)

class StatisticsModel(
        val duration: String,
        val bytesReceived: FormattedBytesViewItem,
        val bytesSent: FormattedBytesViewItem
) {
    companion object {
        fun from(stats: Statistics): StatisticsModel {
            return StatisticsModel(
                    duration = UnitFormatter.timeDisplay(stats.duration),
                    bytesReceived = UnitFormatter.bytesDisplay(stats.bytesReceived),
                    bytesSent = UnitFormatter.bytesDisplay(stats.bytesSent)
            )
        }
    }
}

class SharedViewModel(
        private val nodeRepository: NodeRepository,
        private val mysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
        private val notificationManager: AppNotificationManager,
        private val walletViewModel: WalletViewModel
) : ViewModel() {

    val selectedProposal = MutableLiveData<ProposalViewItem>()
    val connectionState = MutableLiveData<ConnectionState>()
    val statistics = MutableLiveData<StatisticsModel>()
    val location = MutableLiveData<LocationModel>()

    private var isConnected = false

    suspend fun load() {
        initListeners()
        loadActiveProposal()
        loadLocation()
        loadCurrentStatus()
    }

    fun selectProposal(item: ProposalViewItem) {
        selectedProposal.value = item
    }

    fun canConnect(): Boolean {
        val state = connectionState.value
        return state == null || state == ConnectionState.NOT_CONNECTED || state == ConnectionState.UNKNOWN
    }

    fun isConnected(): Boolean {
        val state = connectionState.value
        return state != null && state == ConnectionState.CONNECTED
    }

    suspend fun connect(identityAddress: String, providerID: String, serviceType: String) {
        try {
            connectionState.value = ConnectionState.CONNECTING
            // Before doing actual connection add some delay to prevent
            // from trying to establish connection if user instantly clicks CANCEL.
            delay(100)
            val req = ConnectRequest()
            req.identityAddress = identityAddress
            req.providerID = providerID
            req.serviceType = serviceType
            nodeRepository.connect(req)

            // Force app to run in foreground while connected to VPN.
            val coreService = mysteriumCoreService.await()
            coreService.setActiveProposal(selectedProposal.value!!)
            coreService.startForegroundWithNotification(
                    notificationManager.defaultNotificationID,
                    notificationManager.createConnectedToVPNNotification()
            )
            isConnected = true
            connectionState.value = ConnectionState.CONNECTED
            loadLocation()
        } catch (e: Exception) {
            isConnected = false
            connectionState.value = ConnectionState.NOT_CONNECTED
            throw e
        }
    }

    suspend fun disconnect() {
        try {
            connectionState.value = ConnectionState.DISCONNECTING
            nodeRepository.disconnect()
            isConnected = false
            connectionState.value = ConnectionState.NOT_CONNECTED
            mysteriumCoreService.await().setActiveProposal(null)
            resetStatistics()
            loadLocation()
        } catch (e: Exception) {
            connectionState.value = ConnectionState.NOT_CONNECTED
            throw e
        } finally {
            mysteriumCoreService.await().stopForeground()
        }
    }

    private suspend fun loadCurrentStatus(): Status? {
        return try {
            val status = nodeRepository.status()
            val state = ConnectionState.parse(status.state)
            connectionState.value = state
            status
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load current status", e)
            null
        }
    }

    private suspend fun loadActiveProposal() {
        val proposal = mysteriumCoreService.await().getActiveProposal()
        if (proposal != null) {
            selectProposal(proposal)
            connectionState.value == ConnectionState.CONNECTED
        }
    }

    // initListeners subscribes to go node library exposed callbacks for statistics and state.
    private suspend fun initListeners() {
        nodeRepository.registerConnectionStatusChangeCallback {
            handleConnectionStatusChange(it)
        }

        nodeRepository.registerStatisticsChangeCallback {
            handleStatisticsChange(it)
        }
    }

    private fun handleConnectionStatusChange(status: String) {
        val newState = ConnectionState.parse(status)
        val currentState = connectionState.value

        // Update all UI related state in new coroutine on UI thread.
        // This is needed since status change can be executed on separate
        // inside go node library.
        viewModelScope.launch {
            connectionState.value = newState
            if (currentState == ConnectionState.CONNECTED && newState == ConnectionState.NOT_CONNECTED) {
                notificationManager.showConnectionLostNotification()
                resetStatistics()
                loadLocation()
            }
        }
    }

    private fun handleStatisticsChange(stats: Statistics) {
        // Update all UI related state in new coroutine on UI thread.
        // This is needed since status change can be executed on separate
        // inside go node library.
        viewModelScope.launch {
            val s = StatisticsModel.from(stats)
            statistics.value = StatisticsModel.from(stats)

            if (isConnected() && walletViewModel.needToTopUp()) {
                notificationManager.showTopUpBalanceNotification()
            }

            // Show global notification with connected country and statistics.
            // At this point we need to check if proposal is not null since
            // statistics event can fire sooner than proposal is loaded.
            if (selectedProposal.value != null && isConnected()) {
                val countryName = selectedProposal.value?.countryName
                val notificationTitle = "Connected to $countryName"
                val notificationContent = "Received ${s.bytesReceived.value} ${s.bytesReceived.units} | Send ${s.bytesSent.value} ${s.bytesSent.units}"
                notificationManager.showStatisticsNotification(notificationTitle, notificationContent)
            }
        }
    }

    private suspend fun loadLocation() {
        // Try to load location with few attempts. It can fail to load when connected to VPN.
        location.value = LocationModel(ip = "Updating", countryFlagImage = null)
        for (i in 1..3) {
            try {
                val loc = nodeRepository.location()
                location.value = LocationModel(ip = loc.ip, countryFlagImage = Countries.bitmaps[loc.countryCode.toLowerCase()])
                break
            } catch (e: Exception) {
                delay(1000)
                Log.e(TAG, "Failed to load location. Attempt $i.", e)
            }
        }
    }

    private fun resetStatistics() {
        statistics.value = StatisticsModel.from(Statistics(0, 0, 0))
    }

    companion object {
        const val TAG = "SharedViewModel"
    }
}
