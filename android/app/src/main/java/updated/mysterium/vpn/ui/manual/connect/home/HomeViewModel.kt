package updated.mysterium.vpn.ui.manual.connect.home

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class HomeViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val locationUseCase = useCaseProvider.location()

    fun getLocation() = liveDataResult {
        locationUseCase.getLocation()
    }
}
