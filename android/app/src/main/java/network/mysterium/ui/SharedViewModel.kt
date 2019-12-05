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
import kotlinx.coroutines.*
import mysterium.ConnectRequest
import network.mysterium.db.FavoriteProposal
import network.mysterium.service.core.NodeRepository
import network.mysterium.service.core.Statistics
import network.mysterium.logging.BugReporter
import network.mysterium.service.core.MysteriumCoreService
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

class LocationViewItem(
        val ip: String,
        val countryFlagImage: Bitmap?
)

class StatisticsViewItem(
        val duration: String,
        val bytesReceived: FormattedBytesViewItem,
        val bytesSent: FormattedBytesViewItem
) {
    companion object {
        fun from(it: Statistics): StatisticsViewItem {
            return StatisticsViewItem(
                    duration = UnitFormatter.timeDisplay(it.duration.toDouble()),
                    bytesReceived = UnitFormatter.bytesDisplay(it.bytesReceived.toDouble()),
                    bytesSent = UnitFormatter.bytesDisplay(it.bytesSent.toDouble())
            )
        }
    }
}

class SharedViewModel(
        private val nodeRepository: NodeRepository,
        private val bugReporter: BugReporter,
        private val mysteriumCoreService: CompletableDeferred<MysteriumCoreService>
) : ViewModel() {

    val selectedProposal = MutableLiveData<ProposalViewItem>()
    val connectionState = MutableLiveData<ConnectionState>()
    val statistics = MutableLiveData<StatisticsViewItem>()
    val location = MutableLiveData<LocationViewItem>()

    private var isConnected = false

    suspend fun load(favoriteProposals: Map<String, FavoriteProposal>) {
        unlockIdentity()
        initListeners()
        loadLocation()
        val status = loadCurrentStatus()
        loadActiveProposal(status, favoriteProposals)
    }

    fun selectProposal(item: ProposalViewItem) {
        selectedProposal.value = item
    }

    fun canConnect(): Boolean {
        val state = connectionState.value
        return state == null || state == ConnectionState.NOT_CONNECTED || state == ConnectionState.UNKNOWN
    }

    fun canDisconnect(): Boolean {
        val state = connectionState.value
        return state != null && state == ConnectionState.CONNECTED
    }

    suspend fun connect(providerID: String, serviceType: String) {
        try {
            connectionState.value = ConnectionState.CONNECTING
            // Before doing actual connection add some delay to prevent
            // from trying to establish connection if user instantly clicks CANCEL.
            delay(1000)
            val req = ConnectRequest()
            req.providerID = providerID
            req.serviceType = serviceType
            nodeRepository.connect(req)
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
            resetStatistics()
            loadLocation()
        } catch (e: Exception) {
            connectionState.value = ConnectionState.NOT_CONNECTED
            throw e
        } finally {
            mysteriumCoreService.await().hideNotifications()
        }
    }

    private suspend fun loadCurrentStatus(): Status? {
        return try {
            val status = nodeRepository.getStatus()
            val state = ConnectionState.parse(status.state)
            connectionState.value = state
            status
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load current status", e)
            null
        }
    }

    private suspend fun loadActiveProposal(it: Status?, favoriteProposals: Map<String, FavoriteProposal>) {
        if (it == null || it.providerID == "" || it.serviceType == "") {
            return
        }

        try {
            val proposal = nodeRepository.getProposal(it.providerID, it.serviceType) ?: return
            val proposalViewItem = ProposalViewItem.parse(proposal, favoriteProposals)
            selectProposal(proposalViewItem)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load active proposal", e)
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

    private fun handleConnectionStatusChange(it: String) {
        val newState = ConnectionState.parse(it)
        val currentState = connectionState.value

        // Update all UI related state in new coroutine on UI thread.
        // This is needed since status change can be executed on separate
        // inside go node library.
        viewModelScope.launch {
            connectionState.value = newState
            if (currentState == ConnectionState.CONNECTED && newState != currentState) {
                mysteriumCoreService.await().showNotification("Connection lost", "VPN connection was closed.")
                resetStatistics()
                loadLocation()
            }
        }
    }

    private fun handleStatisticsChange(it: Statistics) {
        // Update all UI related state in new coroutine on UI thread.
        // This is needed since status change can be executed on separate
        // inside go node library.
        viewModelScope.launch {
            val s = StatisticsViewItem.from(it)
            statistics.value = StatisticsViewItem.from(it)

            // Show global notification with connected country and statistics.
            // At this point we need to check if proposal is not null since
            // statistics event can fire sooner than proposal is loaded.
            if (selectedProposal.value != null) {
                val countryName = selectedProposal.value?.countryName
                val notificationTitle = "Connected to $countryName"
                val notificationContent = "Received ${s.bytesReceived.value} ${s.bytesReceived.units} | Send ${s.bytesSent.value} ${s.bytesSent.units}"
                mysteriumCoreService.await().showNotification(notificationTitle, notificationContent)
            }
        }
    }

    private suspend fun unlockIdentity() {
        try {
            val identity = nodeRepository.unlockIdentity()
            bugReporter.setUserIdentifier(identity)
        } catch (e: Exception) {
            Log.e(TAG, "Failed not unlock identity", e)
        }
    }

    private suspend fun loadLocation() {
        // Try to load location with few attempts. It can fail to load when connected to VPN.
        location.value = LocationViewItem(ip = "Updating", countryFlagImage = null)
        for (i in 1..3) {
            try {
                val loc = nodeRepository.getLocation()
                location.value = LocationViewItem(ip = loc.ip, countryFlagImage = Countries.bitmaps[loc.countryCode.toLowerCase()])
                break
            } catch (e: Exception) {
                delay(1000)
                Log.e(TAG, "Failed to load location. Attempt $i.", e)
            }
        }
    }

    private fun resetStatistics() {
        statistics.value = StatisticsViewItem.from(Statistics(0, 0, 0))
    }

    companion object {
        const val TAG = "SharedViewModel"
    }
}
