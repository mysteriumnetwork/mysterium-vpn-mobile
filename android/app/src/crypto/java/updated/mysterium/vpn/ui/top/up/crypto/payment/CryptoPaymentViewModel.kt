package updated.mysterium.vpn.ui.top.up.crypto.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CryptoPaymentViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

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
    private val loginUseCase = useCaseProvider.login()
    private val _paymentSuccessfully = MutableLiveData<Unit>()
    private val _paymentExpired = MutableLiveData<Unit>()
    private val _paymentFailed = MutableLiveData<Unit>()
    private val _paymentCanceled = MutableLiveData<Unit>()
    private var orderId: String? = null

    fun createPaymentOrder(
        currency: String,
        country: String,
        state: String,
        amountUSD: Double,
        isLightning: Boolean
    ) = liveDataResult {
        registerOrderCallback()
        val order = paymentUseCase.createCoingatePaymentGatewayOrder(
            currency,
            country,
            state,
            connectionUseCase.getIdentityAddress(),
            amountUSD,
            isLightning
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

    fun channelAddress() = liveDataResult {
        connectionUseCase.getIdentity().channelAddress
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
                    else -> {}
                }
            }
        }
    }

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

}
