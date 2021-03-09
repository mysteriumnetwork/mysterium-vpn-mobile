package updated.mysterium.vpn.ui.profile

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ProfileViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val connectionUseCase = useCaseProvider.connection()

    fun getIdentity() = liveDataResult {
        connectionUseCase.getIdentity()
    }
}
