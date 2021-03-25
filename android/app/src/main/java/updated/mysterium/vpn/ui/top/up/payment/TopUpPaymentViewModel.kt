package updated.mysterium.vpn.ui.top.up.payment

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpPaymentViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()

    fun createPaymentOrder(
        currency: String,
        mystAmount: Double,
        isLighting: Boolean
    ) = liveDataResult {
        val identity = connectionUseCase.getIdentity()
        paymentUseCase.createPaymentOrder(
            currency,
            identity.address,
            mystAmount,
            isLighting
        )
    }
}
