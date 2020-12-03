package network.mysterium.wallet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mysterium.CreateOrderRequest
import network.mysterium.payment.Currency
import network.mysterium.service.core.NodeRepository
import network.mysterium.service.core.Order
import java.lang.RuntimeException
import java.math.BigDecimal

class WalletTopupViewModel(private val nodeRepository: NodeRepository) : ViewModel() {

    val identity = MutableLiveData<IdentityModel>()
    val currencies = MutableLiveData<List<Currency>>()
    val currenciesLoading = MutableLiveData<Boolean>()

    val topupAmount = MutableLiveData<BigDecimal>()
    val topupAmountValid = Transformations.map(topupAmount) {
        it != null && it > BigDecimal.ZERO
    }
    val currency = MutableLiveData<Currency>()
    val lightning = MutableLiveData<Boolean>()
    val currencyValid = Transformations.map(currency) {
        it != null
    }
    val order = MutableLiveData<Order>()

    fun togglePaymentCurrency(currency: Currency) {
        this.currency.value = when (this.currency.value) {
            currency -> null
            else -> currency
        }
        this.lightning.value = currency.supportsLightning()
    }

    suspend fun load() {
        loadIdentity()
        currenciesLoading.value = true
        loadCurrencies()
        currenciesLoading.value = false

        nodeRepository.registerOrderUpdatedCallback { cb ->
            val o = this.order.value ?: return@registerOrderUpdatedCallback
            if (o.id != cb.orderID) {
                return@registerOrderUpdatedCallback
            }

            val newOrder = o.copy(
                    payAmount = cb.payAmount,
                    payCurrency = cb.payCurrency,
                    status = cb.status
            )
            Log.i(TAG, "Updated order: $newOrder")
            this.order.value = newOrder
        }
    }

    private suspend fun loadCurrencies() {
        try {
            val currencies = nodeRepository.paymentCurrencies()
            this.currencies.value = currencies
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load payment currencies", e)
        }
    }

    private suspend fun loadIdentity() {
        try {
            // Load node identity and its registration status.
            val nodeIdentity = nodeRepository.getIdentity()
            val identity = IdentityModel(
                    address = nodeIdentity.address,
                    channelAddress = nodeIdentity.channelAddress,
                    status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
            )
            this.identity.value = identity
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load account identity", e)
        }
    }

    suspend fun createPaymentOrder(): Order {
        val currency = this.currency.value ?: throw RuntimeException("Pay currency is not set")
        val identity = this.identity.value ?: throw RuntimeException("Identity address is not set")
        val amount = this.topupAmount.value ?: throw RuntimeException("Total amount is not set")
        val lightning = this.lightning.value
                ?: throw RuntimeException("Lightning network flag is not set")

        val req = CreateOrderRequest().apply {
            this.payCurrency = currency.code
            this.identityAddress = identity.address
            this.mystAmount = amount.toDouble()
            this.lightning = lightning
        }

        Log.i(TAG, "Creating a payment order: $req")
        val order = nodeRepository.createPaymentOrder(req)
        this.order.value = order
        return order
    }

    companion object {
        const val TAG = "WalletTopupViewModel"
    }

}
