package updated.mysterium.vpn.ui.top.up

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class PaymentStatusViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val paymentSuccessfully: LiveData<PaymentStatus>
        get() = _paymentSuccessfully

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()
    private val _paymentSuccessfully = SingleLiveEvent<PaymentStatus>()

    fun getPayment(
        mystAmount: Int,
        countryCode: String,
        currency: String,
        gateway: String
    ) = liveDataResult {
        registerOrderCallback()
        paymentUseCase.createCardinityPaymentGatewayOrder(
            country = countryCode,
            identityAddress = connectionUseCase.getIdentityAddress(),
            mystAmount = mystAmount.toDouble(),
            currency = currency,
            gateway = gateway
        )
    }

    private suspend fun registerOrderCallback() {
        paymentUseCase.paymentOrderCallback {
            _paymentSuccessfully.postValue(PaymentStatus.from(it.status))
        }
    }
}
