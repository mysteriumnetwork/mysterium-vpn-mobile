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
import updated.mysterium.vpn.model.wallet.Identity
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class RegistrationViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "RegistrationViewModel"
    }

    val accountRegistrationResult: LiveData<Boolean>
        get() = _accountRegistrationResult

    val accountRegistrationError: LiveData<Throwable>
        get() = _accountRegistrationError

    private val _accountRegistrationResult = MutableLiveData<Boolean>()
    private val _accountRegistrationError = MutableLiveData<Throwable>()

    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val balanceUseCase = useCaseProvider.balance()

    fun tryRegisterAccount(identity: Identity? = null) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            _accountRegistrationError.postValue(exception)
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
            _accountRegistrationResult.postValue(true)
        } else {
            checkFreeRegistration(identity)
        }
    }

    private suspend fun checkFreeRegistration(identity: IdentityModel) {
        val isRegistrationAvailable = loginUseCase.isFreeRegistrationAvailable(identity.address)
        if (isRegistrationAvailable) {
            registerAccount(identity)
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
            registerAccount(identity)
        } else {
            _accountRegistrationResult.postValue(false)
        }
    }

    private suspend fun registerAccount(identity: IdentityModel) {
        val req = RegisterIdentityRequest().apply {
            identityAddress = identity.address
            token?.let {
                this.token = it
            }
        }
        connectionUseCase.registerIdentity(req)
        connectionUseCase.registrationFees()
        _accountRegistrationResult.postValue(true)
    }
}
