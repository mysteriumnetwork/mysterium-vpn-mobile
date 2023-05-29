package updated.mysterium.vpn.core

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import mysterium.*
import okio.buffer
import okio.source
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.exceptions.*
import updated.mysterium.vpn.model.connection.Status
import updated.mysterium.vpn.model.connection.StatusResponse
import updated.mysterium.vpn.model.identity.MigrateHermesStatus
import updated.mysterium.vpn.model.identity.MigrateHermesStatusResponse
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.CountryInfo
import updated.mysterium.vpn.model.manual.connect.ServiceStatus
import updated.mysterium.vpn.model.nodes.ProposalItem
import updated.mysterium.vpn.model.nodes.ProposalsResponse
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.model.payment.PaymentGateway
import updated.mysterium.vpn.model.payment.Purchase
import updated.mysterium.vpn.model.statistics.Location
import updated.mysterium.vpn.model.statistics.Statistics
import updated.mysterium.vpn.model.wallet.Identity
import updated.mysterium.vpn.model.wallet.IdentityRegistrationFees

// Wrapper around Go mobile node library bindings. It should not change any result
// returned from internal mobile node and instead all mappings should happen in
// ViewModels.
class NodeRepository(var deferredNode: DeferredNode) {

    private val moshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Get available proposals for mobile. Internally on Go side
    // proposals are fetched once and cached but it is possible to refresh cache by
    // passing refresh flag.
    //
    // Note that this method need to deserialize JSON byte array since Go Mobile
    // does not support passing complex slices via it's bridge.
    suspend fun proposals(req: GetProposalsRequest): List<ProposalItem> =
        withContext(Dispatchers.IO) {
            val bytes = getProposals(req)
            val proposalsResponse = parseProposals(bytes)
            if (proposalsResponse?.proposals == null) {
                listOf()
            } else {
                proposalsResponse.proposals
            }
        }

    // GetCountries returns service proposals number per country from API.
    suspend fun countries(req: GetProposalsRequest): List<CountryInfo> =
        withContext(Dispatchers.IO) {
            val bytes = getCountries(req)
            val response = parseCountries(bytes)
            response ?: listOf()
        }

    // Register connection status callback.
    suspend fun registerConnectionStatusChangeCallback(cb: (status: String) -> Unit) {
        withContext(Dispatchers.IO) {
            deferredNode.await().registerConnectionStatusChangeCallback { status -> cb(status) }
        }
    }

    // Register service status callback.
    suspend fun registerServiceStatusChangeCallback(cb: (stats: ServiceStatus) -> Unit) {
        withContext(Dispatchers.IO) {
            deferredNode.await()
                    .registerServiceStatusChangeCallback { service, status ->
                        cb(ServiceStatus(service, status))
                    }
        }
    }

    // Register statistics callback.
    suspend fun registerStatisticsChangeCallback(cb: (stats: Statistics) -> Unit) {
        withContext(Dispatchers.IO) {
            deferredNode.await()
                .registerStatisticsChangeCallback { duration, bytesReceived, bytesSent, tokensSpent ->
                    cb(Statistics(duration, bytesReceived, bytesSent, tokensSpent))
                }
        }
    }

    // Register statistics callback.
    suspend fun registerBalanceChangeCallback(cb: (balance: Double) -> Unit) {
        withContext(Dispatchers.IO) {
            deferredNode.await().registerBalanceChangeCallback { _, balance ->
                cb(balance)
            }
        }
    }

    suspend fun registerOrderUpdatedCallback(cb: (payload: OrderUpdatedCallbackPayload) -> Unit) {
        withContext(Dispatchers.IO) {
            deferredNode.await().registerOrderUpdatedCallback(cb)
        }
    }

    // GatewayClientCallback triggers payment callback for google from client side.
    suspend fun gatewayClientCallback(purchase: Purchase) =
        withContext(Dispatchers.IO) {
            val req = GatewayClientCallbackReq().apply {
                this.identityAddress = purchase.identityAddress
                this.gateway = purchase.gateway.gateway
                this.googlePurchaseToken = purchase.googlePurchaseToken
                this.googleProductID = purchase.googleProductID
            }
            deferredNode.await().gatewayClientCallback(req)
        }

    // Connect to VPN service.
    suspend fun connect(req: ConnectRequest) = withContext(Dispatchers.IO) {
        val res = deferredNode.await().connect(req) ?: return@withContext
        Log.e(TAG, res.errorMessage)
        when (res.errorCode) {
            "InvalidProposal" -> throw ConnectInvalidProposalException(res.errorMessage)
            "InsufficientBalance" -> throw ConnectInsufficientBalanceException(res.errorMessage)
            "Unknown" -> {
                if (res.errorMessage == "connection already exists") {
                    throw ConnectAlreadyExistsException(res.errorMessage)
                } else {
                    throw ConnectUnknownException(res.errorMessage)
                }
            }
        }
    }

    // Disconnect from VPN service.
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        deferredNode.await().disconnect()
    }

    // Unlock identity and return it's address. Internally mobile node will create default identity
    // if it is not created yet.
    suspend fun getIdentity(
        getIdentityRequest: GetIdentityRequest = GetIdentityRequest()
    ): Identity = withContext(Dispatchers.IO + SupervisorJob()) {
        val res = deferredNode.await().getIdentity(getIdentityRequest)
        Identity(
            address = res.identityAddress,
            channelAddress = res.channelAddress,
            registrationStatus = res.registrationStatus
        )
    }

    suspend fun upgradeIdentityIfNeeded(identityAddress: String) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        withContext(Dispatchers.IO + handler) {
            val statusResponse =
                deferredNode.await().migrateHermesStatus(identityAddress).decodeToString()
            val status =
                MigrateHermesStatus.from(MigrateHermesStatusResponse.fromJSON(statusResponse))
            if (status == MigrateHermesStatus.REQUIRED) {
                deferredNode.await().migrateHermes(identityAddress)
            }
        }
    }

    // Get registration fees.
    suspend fun identityRegistrationFees() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().identityRegistrationFees
        IdentityRegistrationFees(fee = res.fee)
    }

    suspend fun createPlayBillingPaymentGatewayOrder(req: CreatePaymentGatewayOrderReq): Order =
        withContext(Dispatchers.IO) {
            try {
                val order = deferredNode.await().createPaymentGatewayOrder(req).decodeToString()
                Log.d(TAG, "createPaymentOrder response: $order")
                Order.fromJSON(order) ?: error("Could not parse JSON: $order")
            } catch (exception: Exception) {
                throw BaseNetworkException.fromException(exception)
            }
        }

    suspend fun createCoingatePaymentGatewayOrder(req: CreatePaymentGatewayOrderReq): Order =
        withContext(Dispatchers.IO) {
            try {
                val order = deferredNode.await().createPaymentGatewayOrder(req).decodeToString()
                Order.fromJSON(order) ?: error("Could not parse JSON: $order")
            } catch (exception: Exception) {
                throw BaseNetworkException.fromException(exception)
            }
        }

    suspend fun createCardPaymentGatewayOrder(req: CreatePaymentGatewayOrderReq): Order =
        withContext(Dispatchers.IO) {
            try {
                val order = deferredNode.await().createPaymentGatewayOrder(req).decodeToString()
                Order.fromJSON(order) ?: error("Could not parse JSON: $order")
            } catch (exception: Exception) {
                throw BaseNetworkException.fromException(exception)
            }
        }

    suspend fun listOrders(req: ListOrdersRequest) = withContext(Dispatchers.IO) {
        val orders = deferredNode.await().listPaymentGatewayOrders(req)
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

    // GetStatus returns current connection state and provider info if connected to VPN.
    suspend fun status() = withContext(Dispatchers.IO) {
        val bytes = getStatus()
        val response = parseStatus(bytes)
        response ?: Status(ConnectionState.NOTCONNECTED)
    }

    // Get current balance.
    suspend fun balance(req: GetBalanceRequest) = withContext(Dispatchers.IO) {
        val res = deferredNode.await().getBalance(req)
        res.balance
    }

    // Send user feedback.
    suspend fun sendFeedback(req: SendFeedbackRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().sendFeedback(req)
    }

    suspend fun getExchangeRate(currency: String) = withContext(Dispatchers.IO) {
        deferredNode.await().exchangeRate(currency)
    }

    suspend fun getLastSessions(sessionFilter: SessionFilter): ByteArray =
        withContext(Dispatchers.IO) {
            deferredNode.await().listConsumerSessions(sessionFilter)
        }

    suspend fun downloadPrivateKey(
        identityAddress: String, newPassphrase: String
    ): ByteArray = withContext(Dispatchers.IO) {
        deferredNode.await().exportIdentity(identityAddress, newPassphrase)
    }

    suspend fun exportIdentity(
        address: String, newPassphrase: String
    ): ByteArray = withContext(Dispatchers.IO + SupervisorJob()) {
        deferredNode.await().exportIdentity(address, newPassphrase)
    }

    suspend fun importIdentity(
        privateKey: ByteArray, passphrase: String
    ): String = withContext(Dispatchers.IO + SupervisorJob()) {
        deferredNode.await().importIdentity(privateKey, passphrase)
    }

    suspend fun getWalletEquivalent(balance: Double): Estimates = withContext(Dispatchers.IO) {
        deferredNode.await().calculateEstimates(balance)
    }

    suspend fun getResidentCountry(): String = withContext(Dispatchers.IO) {
        deferredNode.await().residentCountry()
    }

    suspend fun saveResidentCountry(
        residentCountryUpdateRequest: ResidentCountryUpdateRequest
    ) = withContext(Dispatchers.IO) {
        deferredNode.await().updateResidentCountry(residentCountryUpdateRequest)
    }

    suspend fun getFilterPresets(): ByteArray = withContext(Dispatchers.IO) {
        deferredNode.await().listProposalFilterPresets()
    }

    suspend fun getRegistrationTokenReward(registrationToken: String) =
        withContext(Dispatchers.IO) {
            deferredNode.await().registrationTokenReward(registrationToken)
        }

    suspend fun isFreeRegistrationEligible(address: String) = withContext(Dispatchers.IO) {
        deferredNode.await().isFreeRegistrationEligible(address)
    }

    suspend fun forceBalanceUpdate(req: GetBalanceRequest): GetBalanceResponse =
        withContext(Dispatchers.IO) {
            deferredNode.await().forceBalanceUpdate(req)
        }

    suspend fun getGateways() = withContext(Dispatchers.IO) {
        val request = GetGatewaysRequest().apply {
            optionsCurrency = "USD"
        }
        val gateways = deferredNode.await().getGateways(request)
        PaymentGateway.listFromJSON(
            gateways.decodeToString()
        ) ?: error("Could not parse JSON: $gateways")
    }

    private suspend fun getProposals(req: GetProposalsRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().getProposals(req)
    }

    private suspend fun parseProposals(bytes: ByteArray) = withContext(Dispatchers.Default) {
        kotlin.runCatching {
            moshi
                .adapter(ProposalsResponse::class.java)
                .fromJson(bytes.inputStream().source().buffer())
        }.getOrNull()
    }

    private suspend fun getCountries(req: GetProposalsRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().getCountries(req)
    }

    private suspend fun parseCountries(bytes: ByteArray) = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            moshi
                .adapter<MutableMap<String, Int>>(
                    Types.newParameterizedType(
                        MutableMap::class.java,
                        String::class.java,
                        Int::class.javaObjectType,
                    )
                )
                .fromJson(bytes.inputStream().source().buffer())?.mapNotNull {
                    CountryInfo.from(it.key, it.value)
                }
        }.getOrNull()
    }

    private suspend fun getStatus() = withContext(Dispatchers.IO) {
        deferredNode.await().status
    }

    private suspend fun parseStatus(bytes: ByteArray) = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            moshi
                .adapter(StatusResponse::class.java)
                .fromJson(bytes.inputStream().source().buffer())?.let {
                    Status(it)
                }
        }.getOrNull()
    }

}
