package updated_mysterium_vpn.ui.manual_connect

import androidx.lifecycle.ViewModel
import updated_mysterium_vpn.common.extensions.liveDataResult
import updated_mysterium_vpn.network.provider.usecase.UseCaseProvider

class ManualConnectViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val locationUseCase = useCaseProvider.location()

    fun getLocation() = liveDataResult { locationUseCase.getLocation() }
}
