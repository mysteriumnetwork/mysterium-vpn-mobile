package updated.mysterium.vpn.ui.top.up

import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.liveDataResult
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
            } catch (e: Throwable) {
                Log.d(TAG, "Collection complete")
            }
            Log.d(TAG, "Collection Coroutine Scope Exited")
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