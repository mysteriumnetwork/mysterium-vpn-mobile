package updated.mysterium.vpn.ui.top.up.coingate.payment

import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.top.up.card.summary.BillingDataSource

class TopUpPaymentViewModel(
    useCaseProvider: UseCaseProvider,
    val billingDataSource: BillingDataSource
) : ViewModel() {

    val paymentSuccessfully: LiveData<Unit>
        get() = _paymentSuccessfully

    val paymentExpired: LiveData<Unit>
        get() = _paymentExpired

    val paymentFailed: LiveData<Unit>
        get() = _paymentFailed

    val paymentCanceled: LiveData<Unit>
        get() = _paymentCanceled

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private val pushyUseCase = useCaseProvider.pushy()
    private val _paymentSuccessfully = MutableLiveData<Unit>()
    private val _paymentExpired = MutableLiveData<Unit>()
    private val _paymentFailed = MutableLiveData<Unit>()
    private val _paymentCanceled = MutableLiveData<Unit>()
    private var orderId: Long? = null

    companion object {
        val filterRange = ('0'..'9') + ('.')
    }

    init {
        subscribeOnBillingFlow()
    }

    fun createPaymentOrder(
        currency: String,
        mystAmount: Double,
        isLighting: Boolean
    ) = liveDataResult {
        registerOrderCallback()
        val order = paymentUseCase.createPaymentOrder(
            currency,
            connectionUseCase.getIdentityAddress(),
            mystAmount,
            isLighting
        )
        orderId = order.id
        order
    }

    fun clearPopUpTopUpHistory() {
        balanceUseCase.clearBalancePopUpHistory()
        balanceUseCase.clearMinBalancePopUpHistory()
        balanceUseCase.clearBalancePushHistory()
        balanceUseCase.clearMinBalancePushHistory()
    }

    fun updateLastCurrency(currency: String) {
        pushyUseCase.updateCryptoCurrency(currency)
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
        val usdEquivalent = balanceUseCase.getUsdEquivalent()
        billingDataSource.skuDetailsList.map { list ->
            list?.let {
                toTopUpPriceCardItem(
                    it,
                    usdEquivalent
                )
            }
        }
    }

    private fun toTopUpPriceCardItem(
        list: List<SkuDetails>,
        chfEquivalent: Double
    ): List<TopUpPriceCardItem> {
        return list.map { skuDetails ->
            val price = skuDetails.description.filter { it in filterRange }.toDouble()
            val mystEquivalent = price * chfEquivalent
            TopUpPriceCardItem(
                sku = skuDetails.sku,
                title = skuDetails.price,
                price = price,
                mystEquivalent = mystEquivalent,
                isSelected = list.indexOf(skuDetails) == 0
            )
        }
    }

    private suspend fun registerOrderCallback() {
        paymentUseCase.paymentOrderCallback {
            if (orderId.toString() == it.orderID) {
                when (PaymentStatus.from(it.status)) {
                    PaymentStatus.STATUS_PAID -> _paymentSuccessfully.postValue(Unit)
                    PaymentStatus.STATUS_EXPIRED -> _paymentExpired.postValue(Unit)
                    PaymentStatus.STATUS_CANCELED -> _paymentCanceled.postValue(Unit)
                    PaymentStatus.STATUS_INVALID, PaymentStatus.STATUS_REFUNDED -> {
                        _paymentFailed.postValue(Unit)
                    }
                }
            }
        }
    }
}
