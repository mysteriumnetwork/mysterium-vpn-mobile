package updated.mysterium.vpn.ui.wallet.top.up

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpsListViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val statisticUseCase = useCaseProvider.statistic()

    fun getTopUps() = liveDataResult {
        statisticUseCase.getTopUps().filter { order ->
            order.paid
        }
    }
}
