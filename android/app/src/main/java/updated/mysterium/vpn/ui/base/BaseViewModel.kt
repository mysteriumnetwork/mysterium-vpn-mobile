package updated.mysterium.vpn.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.Locale

class BaseViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val BALANCE_LIMIT = 1.0
        const val MIN_BALANCE_LIMIT = BALANCE_LIMIT * 0.1
    }

    val balanceRunningOut: LiveData<Double>
        get() = _balanceRunningOut

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    val insufficientFunds: LiveData<Unit>
        get() = _insufficientFunds

    private val _balanceRunningOut = MutableLiveData<Double>()
    private val _connectionState = MutableLiveData<ConnectionState>()
    private val _insufficientFunds = MutableLiveData<Unit>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val settingsUseCase = useCaseProvider.settings()

    init {
        balanceListener()
        connectionListener()
    }

    fun checkCurrentConnection() {
        viewModelScope.launch {
            val status = connectionUseCase.status()
            val connectionModel = ConnectionState.valueOf(status.state.toUpperCase(Locale.ROOT))
            _connectionState.postValue(connectionModel)
        }
    }

    fun isHintAlreadyShown() = settingsUseCase.isConnectionHintShown()

    fun hintShown() = settingsUseCase.connectionHintShown()

    private fun balanceListener() {
        viewModelScope.launch {
            balanceUseCase.initBalanceListener {
                if (it < BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isBalancePopUpShown()) {
                    _balanceRunningOut.postValue(it)
                    balanceUseCase.balancePopUpShown()
                }
                if (it < MIN_BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isMinBalancePopUpShown()) {
                    _balanceRunningOut.postValue(it)
                    balanceUseCase.minBalancePopUpShown()
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
                val connectionStateModel = ConnectionState.valueOf(it.toUpperCase(Locale.ROOT))
                _connectionState.postValue(connectionStateModel)
            }
        }
    }
}
