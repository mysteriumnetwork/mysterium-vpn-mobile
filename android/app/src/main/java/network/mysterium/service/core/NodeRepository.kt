package network.mysterium.service.core

import android.os.Parcelable
import android.util.Log
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mysterium.*
import network.mysterium.payment.Currency
import network.mysterium.payment.Order
import java.math.BigDecimal
import java.math.RoundingMode.*

class ProposalItem(
    @Json(name = "providerId")
    val providerID: String,

    @Json(name = "serviceType")
    val serviceType: String,

    @Json(name = "countryCode")
    val countryCode: String,

    @Json(name = "qualityLevel")
    val qualityLevel: Int,

    @Json(name = "nodeType")
    val nodeType: String = "",

    @Json(name = "monitoringFailed")
    val monitoringFailed: Boolean,

    @Json(name = "payment")
    val payment: ProposalPaymentMethod
)

@Parcelize
class ProposalPaymentMethod(
    @Json(name = "type")
    val type: String,

    @Json(name = "price")
    val price: ProposalPaymentMoney,

    @Json(name = "rate")
    val rate: ProposalPaymentRate
) : Parcelable

@Parcelize
class ProposalPaymentMoney(
    @Json(name = "amount")
    val amount: Double,

    @Json(name = "currency")
    val currency: String
): Parcelable

@Parcelize
class ProposalPaymentRate(
    @Json(name = "perSeconds")
    val perSeconds: Double,

    @Json(name = "perBytes")
    val perBytes: Double
): Parcelable

class ProposalsResponse(
    @Json(name = "proposals")
    val proposals: List<ProposalItem>?
)

class ProposalResponse(
    @Json(name = "proposal")
    val proposal: ProposalItem?
)

class Statistics(
    val duration: Long,
    val bytesReceived: Long,
    val bytesSent: Long,
    val tokensSpent: Double
)

class Location(
    val ip: String,
    val countryCode: String
)

class Status(
    val state: String,
    val providerID: String,
    val serviceType: String
)

class Identity(
    val address: String,
    val channelAddress: String,
    val registrationStatus: String
)

class IdentityRegistrationFees(
    val fee: Double
)

class HealthData(
    val version: String
)

class PriceSettings(config: ConsumerPaymentConfig) {
    val perGibMax: BigDecimal
    val defaultPerGib: BigDecimal
    val perMinuteMax: BigDecimal
    val defaultMinute: BigDecimal
    val perHourMax: BigDecimal
    val defaultHour: BigDecimal

    init {
        val perGibDecimal = config.pricePerGIBMax
            .toBigDecimal()
            .divide(DECIMAL_PART.toBigDecimal())
        perGibMax = perGibDecimal
        defaultPerGib = perGibDecimal.divide(2.toBigDecimal())
        val perMinuteDecimal = config.pricePerMinuteMax
            .toBigDecimal()
            .divide(DECIMAL_PART.toBigDecimal())
        perMinuteMax = perMinuteDecimal
        defaultMinute = perMinuteDecimal.divide(2.toBigDecimal())

        perHourMax = perMinuteMax.multiply(60.toBigDecimal())
        defaultHour = defaultMinute.multiply(60.toBigDecimal())
    }

    companion object {
        const val DECIMAL_PART = 1_000_000_000_000_000_000
    }
}

class ConnectUnknownException(message: String) : Exception(message)
class ConnectInvalidProposalException(message: String) : Exception(message)
class ConnectInsufficientBalanceException(message: String) : Exception(message)

// Wrapper around Go mobile node library bindings. It should not change any result
// returned from internal mobile node and instead all mappings should happen in
// ViewModels.
class NodeRepository(var deferredNode: DeferredNode) {
    // Get available proposals for mobile. Internally on Go side
    // proposals are fetched once and cached but it is possible to refresh cache by
    // passing refresh flag.
    //
    // Note that this method need to deserialize JSON byte array since Go Mobile
    // does not support passing complex slices via it's bridge.
    suspend fun proposals(req: GetProposalsRequest): List<ProposalItem> {
        val bytes = getProposals(req)
        val proposalsResponse = parseProposals(bytes)
        if (proposalsResponse?.proposals == null) {
            return listOf()
        }

        return proposalsResponse.proposals
    }

    // Register connection status callback.
    suspend fun registerConnectionStatusChangeCallback(cb: (status: String) -> Unit) {
        deferredNode.await().registerConnectionStatusChangeCallback { status -> cb(status) }
    }

    // Register statistics callback.
    suspend fun registerStatisticsChangeCallback(cb: (stats: Statistics) -> Unit) {
        deferredNode.await().registerStatisticsChangeCallback { duration, bytesReceived, bytesSent, tokensSpent ->
            cb(Statistics(duration, bytesReceived, bytesSent, tokensSpent))
        }
    }

    // Register statistics callback.
    suspend fun registerBalanceChangeCallback(cb: (balance: Double) -> Unit) {
        deferredNode.await().registerBalanceChangeCallback { _, balance ->
            cb(balance)
        }
    }

    suspend fun getPriceSettings() = withContext(Dispatchers.Default) {
        PriceSettings(deferredNode.await().consumerPaymentConfig)
    }

    // Register identity registration status callback.
    suspend fun registerIdentityRegistrationChangeCallback(cb: (status: String) -> Unit) {
        deferredNode.await().registerIdentityRegistrationChangeCallback { _, status ->
            cb(status)
        }
    }

    suspend fun registerOrderUpdatedCallback(cb: (payload: OrderUpdatedCallbackPayload) -> Unit) {
        deferredNode.await().registerOrderUpdatedCallback(cb)
    }

    // Connect to VPN service.
    suspend fun connect(req: ConnectRequest) = withContext(Dispatchers.IO) {
        val res = deferredNode.await().connect(req) ?: return@withContext

        when (res.errorCode) {
            "InvalidProposal" -> throw ConnectInvalidProposalException(res.errorMessage)
            "InsufficientBalance" -> throw ConnectInsufficientBalanceException(res.errorMessage)
            "Unknown" -> throw ConnectUnknownException(res.errorMessage)
        }
    }

    // Reconnect to VPN service.
    suspend fun reconnect(req: ConnectRequest) = withContext(Dispatchers.IO) {
        val res = deferredNode.await().reconnect(req) ?: return@withContext

        when (res.errorCode) {
            "InvalidProposal" -> throw ConnectInvalidProposalException(res.errorMessage)
            "InsufficientBalance" -> throw ConnectInsufficientBalanceException(res.errorMessage)
            "Unknown" -> throw ConnectUnknownException(res.errorMessage)
        }
    }

    // Disconnect from VPN service.
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        deferredNode.await().disconnect()
    }

    // Unlock identity and return it's address. Internally mobile node will create default identity
    // if it is not created yet.
    suspend fun getIdentity(): Identity = withContext(Dispatchers.IO) {
        val res = deferredNode.await().getIdentity(GetIdentityRequest())
        Identity(address = res.identityAddress, channelAddress = res.channelAddress, registrationStatus = res.registrationStatus)
    }

    // Get registration fees.
    suspend fun identityRegistrationFees() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().identityRegistrationFees
        IdentityRegistrationFees(fee = res.fee)
    }

    suspend fun paymentCurrencies() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().currencies()
        Klaxon().parseArray<String>(res.inputStream())?.map { Currency(it) }
    }

    suspend fun createPaymentOrder(req: CreateOrderRequest) = withContext(Dispatchers.IO) {
        val order = deferredNode.await().createOrder(req).decodeToString()
        Log.d(TAG, "createPaymentOrder response: $order")
        Order.fromJSON(order) ?: error("Could not parse JSON: $order")
    }

    suspend fun getOrder(req: GetOrderRequest) = withContext(Dispatchers.IO) {
        val order = deferredNode.await().getOrder(req)
        Order.fromJSON(order.decodeToString()) ?: error("Could not parse JSON: $order")
    }

    suspend fun listOrders(req: ListOrdersRequest) = withContext(Dispatchers.IO) {
        val orders = deferredNode.await().listOrders(req)
        Order.listFromJSON(orders.decodeToString()) ?: error("Could not parse JSON: $orders")
    }

    // Register identity with given fee.
    suspend fun registerIdentity(req: RegisterIdentityRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().registerIdentity(req)
    }

    // Get current location with country and IP.
    suspend fun location() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().location
        Location(
            ip = res.ip,
            countryCode = res.country
        )
    }

    // Get current  connection status.
    suspend fun status() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().status
        Status(
            state = res.state,
            providerID = res.providerID,
            serviceType = res.serviceType
        )
    }

    // Get current balance.
    suspend fun balance(req: GetBalanceRequest) = withContext(Dispatchers.IO) {
        val res = deferredNode.await().getBalance(req)
        res.balance
    }

    suspend fun healthCheck() = withContext(Dispatchers.IO) {
        val hz = deferredNode.await().healthCheck()
        HealthData(
            version = hz.version
        )
    }

    // Send user feedback.
    suspend fun sendFeedback(req: SendFeedbackRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().sendFeedback(req)
    }

    private suspend fun getProposals(req: GetProposalsRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().getProposals(req)
    }

    private suspend fun parseProposals(bytes: ByteArray) = withContext(Dispatchers.Default) {
        Klaxon().parse<ProposalsResponse>(bytes.inputStream())
    }

    companion object {
        const val TAG = "NodeRepository"
    }
}
