package updated.mysterium.vpn.ui.top.up

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.TopUpCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()
    private val paymentUseCase = useCaseProvider.payment()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }
    fun getUsdPrices(gateway: Gateway) = liveDataResult {
        paymentUseCase.getGateways()
            .find {
                it.name == gateway.gateway
            }
            ?.orderOptions
            ?.amountsSuggestion
            ?.mapIndexed { index, value ->
                TopUpCardItem(value.toString(), index == 0)
            }
    }
}
