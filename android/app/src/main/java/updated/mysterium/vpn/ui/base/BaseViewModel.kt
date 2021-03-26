package updated.mysterium.vpn.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class BaseViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val BALANCE_LIMIT = 1.0
    }

    val balanceRunningOut: LiveData<Double>
        get() = _balanceRunningOut

    private val _balanceRunningOut = MutableLiveData<Double>()
    private val balanceUseCase = useCaseProvider.balance()

    init {
        viewModelScope.launch {
            balanceUseCase.initBalanceListener {
                if (it < BALANCE_LIMIT && it > 0.0 && !balanceUseCase.isBalancePopUpShown()) {
                    _balanceRunningOut.postValue(it)
                    balanceUseCase.balancePopUpShown()
                }
            }
        }
    }
}
