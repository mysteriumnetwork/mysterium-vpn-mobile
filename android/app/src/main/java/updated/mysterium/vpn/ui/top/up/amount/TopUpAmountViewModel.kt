package updated.mysterium.vpn.ui.top.up.amount

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpAmountViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val balanceUseCase = useCaseProvider.balance()
    private var usdEquivalent: Double? = null

    fun getUsdEquivalent(value: Int) = liveDataResult {
        if (usdEquivalent == null) {
            usdEquivalent = balanceUseCase.getUsdEquivalent()
        }
        usdEquivalent?.let {
            it * value
        }
    }
}
