package updated.mysterium.vpn.ui.top.up.card.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.model.payment.CardOrderRequestInfo
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CardSummaryViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val paymentSuccessfully: LiveData<PaymentStatus>
        get() = _paymentSuccessfully

    private val _paymentSuccessfully = SingleLiveEvent<PaymentStatus>()

    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val paymentUseCase = useCaseProvider.payment()
    private val pushyUseCase = useCaseProvider.pushy()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

    fun getPayment(
        info: CardOrderRequestInfo
    ) = liveDataResult {
        registerOrderCallback()
        paymentUseCase.createCardPaymentGatewayOrder(
            country = info.country,
            identityAddress = connectionUseCase.getIdentityAddress(),
            amountUSD = info.amountUsd,
            currency = info.currency,
            gateway = info.gateway
        )
    }

    private suspend fun registerOrderCallback() {
        paymentUseCase.paymentOrderCallback {
            _paymentSuccessfully.postValue(PaymentStatus.from(it.status))
        }
    }

    fun updateLastCurrency(currency: String) {
        pushyUseCase.updateCryptoCurrency(currency)
    }

    fun clearPopUpTopUpHistory() {
        balanceUseCase.clearBalancePopUpHistory()
        balanceUseCase.clearMinBalancePopUpHistory()
        balanceUseCase.clearBalancePushHistory()
        balanceUseCase.clearMinBalancePushHistory()
    }

}
