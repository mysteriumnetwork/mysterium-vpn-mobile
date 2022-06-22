package updated.mysterium.vpn.ui.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import mysterium.GetBalanceRequest
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.wallet.Identity
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.model.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class RegistrationViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val identityRegistrationResult: LiveData<Boolean>
        get() = _identityRegistrationResult

    val identityRegistrationError: LiveData<Throwable>
        get() = _identityRegistrationError

    private val _identityRegistrationResult = MutableLiveData<Boolean>()
    private val _identityRegistrationError = MutableLiveData<Throwable>()

    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val balanceUseCase = useCaseProvider.balance()

    fun registerIdentityWithToken(token: String) = liveDataResult {
        val nodeIdentity = connectionUseCase.getIdentity()
        val identity = IdentityModel(
            address = nodeIdentity.address,
            channelAddress = nodeIdentity.channelAddress,
            status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
        )
        if (!identity.registered) {
            val req = RegisterIdentityRequest().apply {
                identityAddress = identity.address
                this.token = token
            }
            connectionUseCase.registerIdentity(req)
            connectionUseCase.registrationFees()
            _identityRegistrationResult.postValue(true)
        }
    }

    fun tryRegisterIdentity(identity: Identity? = null) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            _identityRegistrationError.postValue(exception)
        }
        viewModelScope.launch(handler) {
            val identityModel = if (identity == null) {
                IdentityModel(connectionUseCase.getIdentity())
            } else {
                IdentityModel(identity)
            }
            checkRegistrationStatus(identityModel)
        }
    }

    private suspend fun checkRegistrationStatus(identity: IdentityModel) {
        if (identity.registered) {
            _identityRegistrationResult.postValue(true)
        } else {
            checkFreeRegistration(identity)
        }
    }

    private suspend fun checkFreeRegistration(identity: IdentityModel) {
        val isRegistrationAvailable = loginUseCase.isFreeRegistrationAvailable(identity.address)
        if (isRegistrationAvailable) {
            registerIdentity(identity)
        } else {
            checkUpdatedBalance(identity)
        }
    }

    private suspend fun checkUpdatedBalance(identity: IdentityModel) {
        val balanceRequest = GetBalanceRequest().apply {
            identityAddress = identity.address
        }
        val balance = balanceUseCase.forceBalanceUpdate(balanceRequest).balance
        if (balance > 0) {
            registerIdentity(identity)
        } else {
            _identityRegistrationResult.postValue(false)
        }
    }

    private suspend fun registerIdentity(identity: IdentityModel) {
        val req = RegisterIdentityRequest().apply {
            identityAddress = identity.address
            token?.let {
                this.token = it
            }
        }
        connectionUseCase.registerIdentity(req)
        connectionUseCase.registrationFees()
        _identityRegistrationResult.postValue(true)
    }

}
