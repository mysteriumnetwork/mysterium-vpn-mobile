package updated.mysterium.vpn.ui.wallet.spendings

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SpendingsViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val statisticUseCase = useCaseProvider.statistic()

    fun getSpendings() = liveDataResult {
        statisticUseCase.getSpendings()
    }
}
