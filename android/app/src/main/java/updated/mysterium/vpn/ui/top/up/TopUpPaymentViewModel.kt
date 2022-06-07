package updated.mysterium.vpn.ui.top.up

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.Purchase
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.top.up.summary.BillingDataSource

class TopUpPaymentViewModel(
    useCaseProvider: UseCaseProvider,
    val billingDataSource: BillingDataSource
) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private val _paymentSuccessfully = MutableLiveData<Unit>()

    companion object {
        val filterRange = ('0'..'9') + ('.')
    }

    init {
        subscribeOnBillingFlow()
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

    fun isBalanceLimitExceeded() = liveDataResult {
        paymentUseCase.isBalanceLimitExceeded()
    }

    private fun subscribeOnBillingFlow() {
        viewModelScope.launch {
            try {
                billingDataSource.getNewPurchases().collect { skuList ->
                    _paymentSuccessfully.postValue(Unit)
                    billingDataSource.refreshPurchases()
                }
            } catch (exception: Throwable) {
                Log.e(TAG, exception.localizedMessage ?: exception.toString())
            }
        }
        viewModelScope.launch {
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
                    .launchIn(this)
            } catch (exception: Throwable) {
                Log.e(TAG, exception.localizedMessage ?: exception.toString())
            }
        }
    }

    fun getSkuDetails() = liveDataResult {
        billingDataSource.skuDetailsList.map { list ->
            list?.let {
                toTopUpPriceCardItem(
                    it
                )
            }
        }
    }

    private fun toTopUpPriceCardItem(
        list: List<SkuDetails>
    ): List<TopUpPriceCardItem> {
        return list.map { skuDetails ->
            val price = skuDetails.description.filter { it in filterRange }.toDouble()
            TopUpPriceCardItem(
                id = "",
                sku = skuDetails.sku,
                price = price,
                isSelected = list.indexOf(skuDetails) == 0
            )
        }
    }

}
