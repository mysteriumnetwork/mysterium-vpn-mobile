package updated.mysterium.vpn.ui.create.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.model.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CreateAccountViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val navigateForward: LiveData<Unit>
        get() = _navigateForward

    val registrationError: LiveData<Exception>
        get() = _registrationError

    private val _navigateForward = MutableLiveData<Unit>()
    private val _registrationError = MutableLiveData<Exception>()
    private val privateKeyUseCase = useCaseProvider.privateKey()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private var newIdentity: IdentityModel? = null

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
            if (newIdentity == null) {
                val nodeIdentity = connectionUseCase.getIdentity()
                newIdentity = IdentityModel(
                    address = nodeIdentity.address,
                    channelAddress = nodeIdentity.channelAddress,
                    status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
                )
            }
            newIdentity?.let {
                loginUseCase.userCreateOrImportAccount(true)
                _navigateForward.postValue(Unit)
            }
        }
    }

    fun accountCreated(isNewUser: Boolean) {
        loginUseCase.accountCreated()
        loginUseCase.userCreateOrImportAccount(isNewUser)
    }

    fun getIdentity() = liveDataResult {
        val identity = connectionUseCase.getIdentity()
        IdentityModel(identity)
    }

    fun accountFlowShown() = loginUseCase.accountFlowShown()

}
