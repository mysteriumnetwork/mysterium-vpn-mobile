package updated.mysterium.vpn.ui.top.up.amount.usd

import android.util.Log
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.TopUpCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpAmountUsdViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()

    fun getAmountsUSD(gateway: Gateway) = liveDataResult {
        val gateways = paymentUseCase.getGateways()
        val suggestions = gateways
            .find { it.name == gateway.gateway }
            ?.orderOptions
            ?.amountsSuggestion
        if (suggestions.isNullOrEmpty()) {
            // Names the gateways the node did offer, which separates "renamed or
            // removed upstream" from "offered, but with no suggested amounts".
            Log.w(
                TAG,
                "No suggested amounts for gateway '${gateway.gateway}'. " +
                    "Node offers: ${gateways.joinToString { it.name }}"
            )
            return@liveDataResult null
        }
        suggestions.mapIndexed { index, amount ->
            TopUpCardItem(amount, index == 0)
        }
    }

}
