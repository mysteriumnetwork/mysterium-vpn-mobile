package updated.mysterium.vpn.ui.top.up.amount.usd

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.TopUpCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpAmountUsdViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()

    fun getAmountsUSD(gateway: Gateway) = liveDataResult {
        paymentUseCase.getGateways()
            .find {
                it.name == gateway.gateway
            }
            ?.orderOptions
            ?.amountsSuggestion
            ?.mapIndexed { index, amount ->
                TopUpCardItem(amount, index == 0)
            }
    }

}
