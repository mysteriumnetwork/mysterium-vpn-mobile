package updated.mysterium.vpn.ui.top.up.coingate.amount

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpAmountViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val balanceUseCase = useCaseProvider.balance()

    fun getWalletEquivalent(balance: Double) = liveDataResult {
        balanceUseCase.getWalletEquivalent(balance)
    }
}
