package updated.mysterium.vpn.ui.top.up

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "TopUpViewModel"
    }

    private val balanceUseCase = useCaseProvider.balance()
    private val loginUseCase = useCaseProvider.login()
    private var usdEquivalent: Double? = null

    init {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            usdEquivalent = balanceUseCase.getUsdEquivalent()
        }
    }

    fun getUsdEquivalent(value: Int) = liveDataResult {
        if (usdEquivalent == null) {
            usdEquivalent = balanceUseCase.getUsdEquivalent()
        }
        usdEquivalent?.let {
            it * value
        }
    }

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }
}
