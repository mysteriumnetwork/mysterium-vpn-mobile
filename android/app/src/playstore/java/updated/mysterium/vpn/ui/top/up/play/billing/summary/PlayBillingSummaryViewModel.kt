package updated.mysterium.vpn.ui.top.up.play.billing.summary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.payment.PlayBillingOrderRequestInfo
import updated.mysterium.vpn.model.payment.Purchase
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

    init {
        subscribeOnBillingFlow()
    }

    private fun subscribeOnBillingFlow() {
        try {
            playBillingDataSource.purchaseConsumedFlow
                .distinctUntilChanged()
                .onEach {
                    val purchase = Purchase(
                        identityAddress = connectionUseCase.getIdentityAddress(),
                        gateway = Gateway.GOOGLE,
                        googlePurchaseToken = it.purchaseToken,
                        googleProductID = it.skus.first()
                    )
                    paymentUseCase.gatewayClientCallback(purchase)
                }
                .launchIn(viewModelScope)
        } catch (exception: Throwable) {
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
    }

    fun getPlayBillingPayment(
        info: PlayBillingOrderRequestInfo
    ) = liveDataResult {
        registerOrderCallback()
        paymentUseCase.createPlayBillingPaymentGatewayOrder(
            identityAddress = connectionUseCase.getIdentityAddress(),
            amountUsd = info.amountUsd,
            country = info.country,
            state = info.state
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

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

}
