package updated.mysterium.vpn.ui.top.up.card.summary

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CardSummaryViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()

    fun getPayment(
        mystAmount: Int,
        countryCode: String,
        currency: String
    ) = liveDataResult {
        paymentUseCase.createPaymentGatewayOrder(
            country = countryCode,
            identityAddress = connectionUseCase.getIdentityAddress(),
            mystAmount = mystAmount.toDouble(),
            currency = currency
        )
    }
}
