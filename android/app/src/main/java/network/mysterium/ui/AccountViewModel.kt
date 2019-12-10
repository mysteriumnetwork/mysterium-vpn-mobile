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

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import network.mysterium.logging.BugReporter
import network.mysterium.service.core.Balance
import network.mysterium.service.core.NodeRepository
import kotlin.math.floor
import kotlin.math.roundToInt

enum class IdentityRegistrationStatus(val status: String) {
    UNKNOWN("Unknown"),
    REGISTERED_CONSUMER("RegisteredConsumer"),
    UNREGISTERED("Unregistered"),
    IN_PROGRESS("InProgress"),
    PROMOTING("Promoting"),
    REGISTRATION_ERROR("RegistrationError");

    companion object {
        fun parse(status: String): IdentityRegistrationStatus {
            return values().find { it.status == status } ?: UNKNOWN
        }
    }
}

class IdentityViewItem(
        val address: String,
        val channelAddress: String,
        var status: IdentityRegistrationStatus
) {
    val registered: Boolean
        get() {
            return status == IdentityRegistrationStatus.REGISTERED_CONSUMER
        }
}

class BalanceViewItem(val value: MoneyViewItem)

class MoneyViewItem(val value: Long = 0) {
    var displayValue = ""

    init {
        displayValue = "${floor(value.toDouble() / 100_000_000).roundToInt()} MYST"
    }
}

class AccountViewModel(private val nodeRepository: NodeRepository, private val bugReporter: BugReporter) : ViewModel() {
    val balance = MutableLiveData<BalanceViewItem>()
    val identity = MutableLiveData<IdentityViewItem>()

    suspend fun load() {
        initListeners()
        loadIdentity()
    }

    suspend fun topUp() {
        try {
            val currentIdentity = identity.value ?: return
            nodeRepository.topUpBalance(currentIdentity.address)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to top-up balance", e)
        }
    }

    fun isIdentityRegistered(): Boolean {
        val currentIdentity = identity.value ?: return false
        return currentIdentity.registered
    }

    private suspend fun initListeners() {
        nodeRepository.registerBalanceChangeCallback {
            handleBalanceChange(it)
        }

        nodeRepository.registerIdentityRegistrationChangeCallback {
            handleIdentityRegistrationChange(it)
        }
    }

    private fun handleIdentityRegistrationChange(it: String) {
        val currentIdentity = identity.value ?: return
        viewModelScope.launch {
            currentIdentity.status = IdentityRegistrationStatus.parse(it)
            identity.value = currentIdentity
        }
    }

    private fun handleBalanceChange(it: Balance) {
        viewModelScope.launch {
            balance.value = BalanceViewItem(MoneyViewItem(it.value))
        }
    }

    private suspend fun loadIdentity() {
        try {
            // Load node identity and it's registration status.
            val nodeIdentity = nodeRepository.getIdentity()
            val identityResult = IdentityViewItem(
                    address = nodeIdentity.address,
                    channelAddress = nodeIdentity.channelAddress,
                    status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
            )
            identity.value = identityResult
            bugReporter.setUserIdentifier(nodeIdentity.address)
            Log.i(TAG, "Loaded identity ${nodeIdentity.address}, channel addr: ${nodeIdentity.channelAddress}")

            if (identityResult.status == IdentityRegistrationStatus.UNREGISTERED || identityResult.status == IdentityRegistrationStatus.REGISTRATION_ERROR) {
                registerIdentity()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load account identity", e)
        }
    }

    private suspend fun registerIdentity() {
        try {
            val registrationFees = nodeRepository.getIdentityRegistrationFees()
            val currentIdentity = identity.value ?: return
            nodeRepository.registerIdentity(currentIdentity.address, registrationFees.fee)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to registered identity", e)
        }
    }

    companion object {
        const val TAG = "AccountViewModel"
    }
}
