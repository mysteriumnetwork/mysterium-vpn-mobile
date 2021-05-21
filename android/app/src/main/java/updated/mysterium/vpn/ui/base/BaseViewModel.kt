package updated.mysterium.vpn.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import updated.mysterium.vpn.common.inline.safeValueOf
import updated.mysterium.vpn.common.languages.LanguagesUtil
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.*

class BaseViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val BALANCE_LIMIT = 0.5
        const val MIN_BALANCE_LIMIT = BALANCE_LIMIT * 0.2
        const val PING_A_SERVER_COMMAND = "/system/bin/ping -c 1 8.8.8.8"
    }

    val balanceRunningOut: LiveData<Boolean>
        get() = _balanceRunningOut

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    val insufficientFunds: LiveData<Unit>
        get() = _insufficientFunds

    val isInternetNotAvailable: LiveData<Boolean>
        get() = _isInternetNotAvailable

    private val _balanceRunningOut = MutableLiveData<Boolean>()
    private val _connectionState = MutableLiveData<ConnectionState>()
    private val _insufficientFunds = MutableLiveData<Unit>()
    private val _isInternetNotAvailable = MutableLiveData<Boolean>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val settingsUseCase = useCaseProvider.settings()
    private var isInternetChecking = false

    init {
        balanceListener()
        connectionListener()
    }

    fun checkCurrentConnection() {
        viewModelScope.launch {
            val status = connectionUseCase.status()
            safeValueOf<ConnectionState>(status.state.toUpperCase(Locale.ROOT))?.let { state ->
                _connectionState.postValue(state)
            }
        }
    }

    fun isHintAlreadyShown() = settingsUseCase.isConnectionHintShown()

    fun hintShown() = settingsUseCase.connectionHintShown()

    fun checkInternetConnection() {
        if (!isInternetChecking) {
            isInternetChecking = true
            val handler = CoroutineExceptionHandler { _, _ ->
                isInternetChecking = false
                _isInternetNotAvailable.postValue(false)
            }
            viewModelScope.launch(handler) {
                val exitValue = withContext(Dispatchers.Default) {
                    val ipProcess = Runtime.getRuntime().exec(PING_A_SERVER_COMMAND)
                    ipProcess.waitFor()
                }
                isInternetChecking = false
                if (exitValue == 0) {
                    _isInternetNotAvailable.postValue(true)
                } else {
                    _isInternetNotAvailable.postValue(false)
                }
            }
        }
    }

    fun firstWarningBalanceShown() {
        balanceUseCase.balancePopUpShown()
    }

    fun secondWarningBalanceShown() {
        balanceUseCase.minBalancePopUpShown()
    }

    fun initUserLocaleLanguage(countryCode: String) = settingsUseCase.userInitialCountryLanguage(
        countryCode = LanguagesUtil.convertUserLanguage(countryCode)
    )

    private fun balanceListener() {
        viewModelScope.launch {
            balanceUseCase.initBalanceListener {
                if (it < BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isBalancePopUpShown()) {
                    _balanceRunningOut.postValue(true)
                }
                if (it < MIN_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isMinBalancePopUpShown()) {
                    _balanceRunningOut.postValue(false)
                }
                if (it == 0.0 && balanceUseCase.isMinBalancePushShown()) {
                    _insufficientFunds.postValue(Unit)
                }
            }
        }
    }

    private fun connectionListener() {
        viewModelScope.launch {
            connectionUseCase.connectionStatusCallback {
                safeValueOf<ConnectionState>(it.toUpperCase(Locale.ROOT))?.let { state ->
                    _connectionState.postValue(state)
                }
            }
        }
    }
}
