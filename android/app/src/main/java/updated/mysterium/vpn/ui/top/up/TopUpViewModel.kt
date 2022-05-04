package updated.mysterium.vpn.ui.top.up

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.TopUpAmountCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

}
