package updated.mysterium.vpn.ui.top.up.play.billing.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.payment.PlayBillingOrderRequestInfo
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class PlayBillingSummaryViewModel(
    useCaseProvider: UseCaseProvider,
    val playBillingDataSource: PlayBillingDataSource
) : ViewModel() {

    val paymentSuccessfully: LiveData<PaymentStatus>
        get() = _paymentSuccessfully

    private val _paymentSuccessfully = SingleLiveEvent<PaymentStatus>()

    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val paymentUseCase = useCaseProvider.payment()

    fun getPlayBillingPayment(
        info: PlayBillingOrderRequestInfo
    ) = liveDataResult {
        registerOrderCallback()
        paymentUseCase.createPlayBillingPaymentGatewayOrder(
            identityAddress = connectionUseCase.getIdentityAddress(),
            amountUsd = info.amountUsd
        )
    }

    private suspend fun registerOrderCallback() {
        paymentUseCase.paymentOrderCallback {
            _paymentSuccessfully.postValue(PaymentStatus.from(it.status))
        }
    }

    fun clearPopUpTopUpHistory() {
        balanceUseCase.clearBalancePopUpHistory()
        balanceUseCase.clearMinBalancePopUpHistory()
        balanceUseCase.clearBalancePushHistory()
        balanceUseCase.clearMinBalancePushHistory()
    }

    fun registerAccount() = liveDataResult {
        val identity = connectionUseCase.getIdentity()
        val identityModel = IdentityModel(identity)
        val req = RegisterIdentityRequest().apply {
            identityAddress = identityModel.address
            token?.let {
                this.token = it
            }
        }
        connectionUseCase.registerIdentity(req)
        connectionUseCase.registrationFees()
    }

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

}
