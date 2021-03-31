package updated.mysterium.vpn.ui.top.up.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class TopUpPaymentViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val STATUS_PAID = "paid"
        const val STATUS_EXPIRED = "expired"
        const val STATUS_INVALID = "invalid"
        const val STATUS_CANCELED = "canceled"
        const val STATUS_REFUNDED = "refunded"
    }

    val paymentSuccessfully: LiveData<Unit>
        get() = _paymentSuccessfully

    val paymentExpired: LiveData<Unit>
        get() = _paymentExpired

    val paymentFailed: LiveData<Unit>
        get() = _paymentFailed

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()
    private val _paymentSuccessfully = MutableLiveData<Unit>()
    private val _paymentExpired = MutableLiveData<Unit>()
    private val _paymentFailed = MutableLiveData<Unit>()

    fun createPaymentOrder(
        currency: String,
        mystAmount: Double,
        isLighting: Boolean
    ) = liveDataResult {
        registerOrderCallback()
        val identity = connectionUseCase.getIdentity()
        paymentUseCase.createPaymentOrder(
            currency,
            identity.address,
            mystAmount,
            isLighting
        )
    }

    private suspend fun registerOrderCallback() {
        paymentUseCase.paymentOrderCallback {
            when (it.status) {
                STATUS_PAID -> _paymentSuccessfully.postValue(Unit)
                STATUS_EXPIRED -> _paymentExpired.postValue(Unit)
                STATUS_INVALID, STATUS_CANCELED, STATUS_REFUNDED -> _paymentFailed.postValue(Unit)
            }
        }
    }
}
