package updated.mysterium.vpn.ui.top.up

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val balanceUseCase = useCaseProvider.balance()
    private val loginUseCase = useCaseProvider.login()
    private var usdEquivalent: Double? = null

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
