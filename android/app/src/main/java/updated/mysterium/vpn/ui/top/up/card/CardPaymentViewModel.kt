package updated.mysterium.vpn.ui.top.up.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CardPaymentViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val USD = "USD"
    }

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()

    fun getPayment(
        mystAmount: Int,
        countryCode: String
    ) = liveDataResult {
        paymentUseCase.createPaymentGatewayOrder(
            country = countryCode,
            identityAddress = connectionUseCase.getIdentityAddress(),
            mystAmount = mystAmount.toDouble(),
            currency = USD
        )
    }
}
