package updated.mysterium.vpn.ui.top.up.card.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.top.up.coingate.payment.TopUpPaymentViewModel

class CardSummaryViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val paymentSuccessfully: LiveData<Unit>
        get() = _paymentSuccessfully

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val _paymentSuccessfully = MutableLiveData<Unit>()

    fun getPayment(
        mystAmount: Int,
        countryCode: String,
        currency: String
    ) = liveDataResult {
        registerOrderCallback()
        paymentUseCase.createPaymentGatewayOrder(
            country = countryCode,
            identityAddress = connectionUseCase.getIdentityAddress(),
            mystAmount = mystAmount.toDouble(),
            currency = currency
        )
    }

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

    private suspend fun registerOrderCallback() {
        paymentUseCase.paymentOrderCallback {
            when (it.status) {
                TopUpPaymentViewModel.STATUS_PAID -> _paymentSuccessfully.postValue(Unit)
            }
        }
    }
}
