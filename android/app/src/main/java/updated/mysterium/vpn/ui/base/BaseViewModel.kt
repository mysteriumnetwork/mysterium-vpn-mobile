package updated.mysterium.vpn.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class BaseViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    companion object {
        const val BALANCE_LIMIT = 0.5
        private const val MIN_BALANCE_LIMIT = BALANCE_LIMIT * 0.2
        private const val PING_A_SERVER_COMMAND = "/system/bin/ping -c 1 8.8.8.8"
    }

    val balanceRunningOut: LiveData<Boolean>
        get() = _balanceRunningOut

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    val insufficientFunds: LiveData<Unit>
        get() = _insufficientFunds

    val isInternetNotAvailable: LiveData<Boolean>
        get() = _isInternetNotAvailable

    val balance: LiveData<Double>
        get() = _balance

    private val _balance = MutableLiveData<Double>()
    private val _balanceRunningOut = SingleLiveEvent<Boolean>()
    private val _connectionState = MutableLiveData<ConnectionState>()
    private val _insufficientFunds = MutableLiveData<Unit>()
    private val _isInternetNotAvailable = SingleLiveEvent<Boolean>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val settingsUseCase = useCaseProvider.settings()
    private var isInternetChecking = false
    private var isDoubleCheck = false

    fun checkCurrentConnection() {
        viewModelScope.launch {
            val status = connectionUseCase.status()
            val state = ConnectionState.from(status.state)
            _connectionState.postValue(state)
        }
    }

    fun isHintAlreadyShown() = settingsUseCase.isConnectionHintShown()

    fun hintShown() = settingsUseCase.connectionHintShown()

    fun checkInternetConnection() {
        if (!isInternetChecking) {
            isInternetChecking = true
            val handler = CoroutineExceptionHandler { _, _ ->
                isInternetChecking = false
                if (!isDoubleCheck) {
                    isDoubleCheck = true
                    checkInternetConnection()
                } else {
                    isDoubleCheck = false
                    _isInternetNotAvailable.postValue(false)
                }
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
                    if (!isDoubleCheck) {
                        isDoubleCheck = true
                        checkInternetConnection()
                    } else {
                        isDoubleCheck = false
                        _isInternetNotAvailable.postValue(false)
                    }
                }
            }
        }
    }

    fun getUserCurrentLanguageCode() = settingsUseCase.getUserSelectedLanguage()

    fun establishListeners() {
        balanceListener()
        connectionListener()
    }

    private fun firstWarningBalanceShown() {
        balanceUseCase.balancePopUpShown()
    }

    private fun secondWarningBalanceShown() {
        balanceUseCase.minBalancePopUpShown()
    }

    private fun balanceListener() {
        viewModelScope.launch {
            balanceUseCase.initBalanceListener {
                _balance.postValue(it)
                if (it < BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isBalancePopUpShown()) {
                    firstWarningBalanceShown()
                    _balanceRunningOut.postValue(true)
                } else if (it < MIN_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isMinBalancePopUpShown()) {
                    secondWarningBalanceShown()
                    _balanceRunningOut.postValue(false)
                } else if (it == 0.0 && balanceUseCase.isMinBalancePushShown()) {
                    _insufficientFunds.postValue(Unit)
                }
            }
        }
    }

    private fun connectionListener() {
        viewModelScope.launch {
            connectionUseCase.connectionStatusCallback {
                val state = ConnectionState.from(it)
                _connectionState.postValue(state)
            }
        }
    }
}
