package updated.mysterium.vpn.ui.monitoring

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class MonitoringViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val statisticUseCase = useCaseProvider.statistic()

    fun getLastSessions() = liveDataResult {
        statisticUseCase.getLastSessions()
    }
}
