package updated.mysterium.vpn.ui.create.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.UserAttributes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import mysterium.RegisterIdentityRequest
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.lang.Exception

class CreateAccountViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "CreateAccountViewModel"
        const val NODE_IDENTITY_KEY = "node_identity"
    }

    val navigateForward: LiveData<Unit>
        get() = _navigateForward

    val registrationError: LiveData<Exception>
        get() = _registrationError

    private val _navigateForward = MutableLiveData<Unit>()
    private val _registrationError = MutableLiveData<Exception>()
    private val privateKeyUseCase = useCaseProvider.privateKey()
    private val connectionUseCase = useCaseProvider.connection()

    fun importAccount(privateKey: String, passphrase: String) = liveDataResult {
        privateKeyUseCase.importIdentity(privateKey, passphrase)
    }

    fun applyNewIdentity(importedIdentity: String) = liveDataResult {
        connectionUseCase.getNewIdentity(importedIdentity)
    }

    fun createNewAccount() {
        val handler = CoroutineExceptionHandler { _, exception ->
            _registrationError.postValue(exception as Exception)
        }
        viewModelScope.launch(handler) {
            val nodeIdentity = connectionUseCase.getIdentity()
            val identity = IdentityModel(
                address = nodeIdentity.address,
                channelAddress = nodeIdentity.channelAddress,
                status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
            )
            registerIntercomClient(identity.address)
            registerIdentity(identity)
        }
    }

    private fun registerIntercomClient(address: String) {
        Intercom.client().apply {
            registerUnidentifiedUser()
            val attrs = UserAttributes.Builder()
                .withCustomAttribute(NODE_IDENTITY_KEY, address)
                .build()
            updateUser(attrs)
        }
    }

    private suspend fun registerIdentity(identity: IdentityModel) {
        if (!identity.registered) {
            val req = RegisterIdentityRequest().apply {
                identityAddress = identity.address
            }
            viewModelScope.launch {
                connectionUseCase.registerIdentity(req)
            }
        }
        loadRegistrationFees()
    }

    private suspend fun loadRegistrationFees() {
        connectionUseCase.registrationFees()
        _navigateForward.postValue(Unit)
    }
}
