package network.mysterium.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import network.mysterium.logging.BugReporter
import network.mysterium.service.core.Balance
import network.mysterium.service.core.NodeRepository

class IdentityViewItem {
    var address: String = ""
    var registered: Boolean = false
}

class BalanceViewItem(val value: MoneyViewItem)

class MoneyViewItem(val value: Long = 0) {
    var displayValue = ""

    // TODO: Handle formatting
    init {
        displayValue = "$value MYST"
    }
}

class AccountViewModel(private val nodeRepository: NodeRepository, private val bugReporter: BugReporter) : ViewModel() {
    val balance = MutableLiveData<BalanceViewItem>()
    val identity = MutableLiveData<IdentityViewItem>()

    suspend fun load() {
        initListeners()
        loadIdentity()
        loadBalance()
    }

    suspend fun topUp() {
        try {
            val currentIdentity = identity.value ?: return
            nodeRepository.topUpBalance(currentIdentity.address)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to top-up balance", e)
            throw e
        }
    }

    private suspend fun initListeners() {
        nodeRepository.registerBalanceChangeCallback {
            handleBalanceChange(it)
        }
    }

    private fun handleBalanceChange(it: Balance) {
        viewModelScope.launch {
            balance.value = BalanceViewItem(MoneyViewItem(it.value))
        }
    }

    private suspend fun loadIdentity() {
        try {
            val identityResult = IdentityViewItem()
            // Load node identity and it's registration status.
            val nodeIdentity = nodeRepository.unlockIdentity()
            val nodeIdentityRegistrationStatus = nodeRepository.getIdentityRegistrationStatus(nodeIdentity.address)
            identityResult.address = nodeIdentity.address
            identityResult.registered = nodeIdentityRegistrationStatus.value == "RegisteredConsumer"
            identity.value = identityResult

            if (!identityResult.registered) {
                registerIdentity()
            }

            bugReporter.setUserIdentifier(nodeIdentity.address)
            Log.i(TAG, "Loaded identity ${nodeIdentity.address}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load account identity", e)
        }
    }

    private suspend fun loadBalance() {
        try {
            val currentIdentity = identity.value ?: return

            if (!currentIdentity.registered) {
                balance.value = BalanceViewItem(MoneyViewItem(0))
                return
            }

            val currentBalance = nodeRepository.getBalance(currentIdentity.address)
            balance.value = BalanceViewItem(MoneyViewItem(currentBalance.value))
            Log.i(TAG, "Loaded balance: ${currentBalance.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load account balance", e)
        }
    }

    private suspend fun registerIdentity() {
        try {
            val registrationFees = nodeRepository.getIdentityRegistrationFees()
            val currentIdentity = identity.value ?: return
            nodeRepository.registerIdentity(currentIdentity.address, registrationFees.fee)
            val updatedIdentity = IdentityViewItem()
            updatedIdentity.address = currentIdentity.address
            updatedIdentity.registered = true
            identity.value = updatedIdentity
            Log.i(TAG, "Identity registered successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to registered identity", e)
        }
    }

    companion object {
        const val TAG = "AccountViewModel"
    }
}
