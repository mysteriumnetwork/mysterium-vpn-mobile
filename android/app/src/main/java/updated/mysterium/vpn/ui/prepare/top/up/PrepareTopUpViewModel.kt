package updated.mysterium.vpn.ui.prepare.top.up

import androidx.lifecycle.ViewModel
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.model.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class PrepareTopUpViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()
    private val tokenUseCase = useCaseProvider.token()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

    fun getRegistrationTokenReward(token: String) = liveDataResult {
        tokenUseCase.getRegistrationTokenReward(token)
    }

}
