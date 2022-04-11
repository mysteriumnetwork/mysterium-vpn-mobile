package updated.mysterium.vpn.ui.top.up

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.TopUpAmountCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()
    private val paymentUseCase = useCaseProvider.payment()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

    fun getAmounts() = liveDataResult {
        paymentUseCase.getGateways()
            .find {
                it.name == Gateway.COINGATE.gateway
            }
            ?.orderOptions
            ?.amountsSuggestion
            ?.mapIndexed { index, value ->
                TopUpAmountCardItem(value.toString(), index == 0)
            }
    }
}
