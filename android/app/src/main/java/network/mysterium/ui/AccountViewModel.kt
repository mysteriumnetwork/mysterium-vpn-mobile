package network.mysterium.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.logging.BugReporter
import network.mysterium.service.core.NodeRepository

class IdentityViewItem {
    var address: String = ""
    var registered: Boolean = false
}

class AccountViewModel(private val nodeRepository: NodeRepository, private val bugReporter: BugReporter) : ViewModel() {
    val identity = CompletableDeferred<IdentityViewItem>()

    suspend fun load() {
        try {
            val identityResult = IdentityViewItem()
            // Load node identity and it's registration status.
            val nodeIdentity = nodeRepository.unlockIdentity()
            val nodeIdentityRegistrationStatus = nodeRepository.getIdentityRegistrationStatus(nodeIdentity.address)
            identityResult.address = nodeIdentity.address
            identityResult.registered = nodeIdentityRegistrationStatus.value == "Registered" // TODO: check for correct status string

            // Set crashlytics user.
            bugReporter.setUserIdentifier(nodeIdentity.address)
        } catch (e: Exception) {
            Log.e(SharedViewModel.TAG, "Failed to load account", e)
        }
    }
}
