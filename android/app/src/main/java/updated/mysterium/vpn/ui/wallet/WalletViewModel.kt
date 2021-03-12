package updated.mysterium.vpn.ui.wallet

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class WalletViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val balanceUseCase = useCaseProvider.balance()

    fun getUsdEquivalent() = liveDataResult {
        balanceUseCase.getUsdEquivalent()
    }
}
