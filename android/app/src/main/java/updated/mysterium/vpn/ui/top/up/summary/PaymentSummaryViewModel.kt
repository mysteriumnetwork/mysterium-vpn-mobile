package updated.mysterium.vpn.ui.top.up.summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.Purchase
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class PaymentSummaryViewModel(
    useCaseProvider: UseCaseProvider,
    val billingDataSource: BillingDataSource
) : ViewModel() {

    init {
        subscribeOnBillingFlow()
    }

    private val loginUseCase = useCaseProvider.login()
    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

    private fun subscribeOnBillingFlow() {
        try {
            billingDataSource.purchaseConsumedFlow
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

}
