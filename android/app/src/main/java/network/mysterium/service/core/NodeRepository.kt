package network.mysterium.service.core

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mysterium.*

class ProposalItem(
        @Json(name = "providerId")
        val providerID: String,

        @Json(name = "serviceType")
        val serviceType: String,

        @Json(name = "countryCode")
        val countryCode: String,

        @Json(name = "qualityLevel")
        val qualityLevel: Int
)

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
        val bytesSent: Long
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
        val address: String
)

class IdentityRegistrationFees(
        val fee: Long
)

class IdentityRegistrationStatus(
        val value: String
)

class Balance(val value: Long)

// Wrapper around Go mobile node library bindings. It should not change any result
// returned from internal mobile node and instead all mappings should happen in
// ViewModels.
class NodeRepository(private val deferredNode: DeferredNode) {

    // Get available proposals for mobile. Internally on Go side
    // proposals are fetched once and cached but it is possible to refresh cache by
    // passing refresh flag.
    //
    // Note that this method need to deserialize JSON byte array since Go Mobile
    // does not support passing complex slices via it's bridge.
    suspend fun getProposals(refresh: Boolean): List<ProposalItem> {
        val req = GetProposalsRequest()
        req.showOpenvpnProposals = true
        req.showWireguardProposals = true
        req.refresh = refresh

        val bytes = getProposals(req)
        val proposalsResponse = parseProposals(bytes)
        if (proposalsResponse?.proposals == null) {
            return listOf()
        }

        return proposalsResponse.proposals
    }

    // Get proposal from cache by given providerID and serviceType.
    //
    // Note that this method need to deserialize JSON byte array since Go Mobile
    // does not support passing complex slices via it's bridge.
    suspend fun getProposal(providerID: String, serviceType: String): ProposalItem? {
        val req = GetProposalRequest()
        req.providerID = providerID
        req.serviceType = serviceType

        val bytes = getProposal(req)
        val proposalsResponse = parseProposal(bytes)
        return proposalsResponse?.proposal
    }

    // Register connection status callback.
    suspend fun registerConnectionStatusChangeCallback(cb: (status: String) -> Unit) {
        deferredNode.await().registerConnectionStatusChangeCallback { status -> cb(status) }
    }

    // Register statistics callback.
    suspend fun registerStatisticsChangeCallback(cb: (stats: Statistics) -> Unit) {
        deferredNode.await().registerStatisticsChangeCallback { duration, bytesReceived, bytesSent ->
            cb(Statistics(duration, bytesReceived, bytesSent))
        }
    }

    // Connect to VPN service.
    suspend fun connect(providerID: String, serviceType: String) = withContext(Dispatchers.IO) {
        val req = ConnectRequest()
        req.providerID = providerID
        req.serviceType = serviceType
        deferredNode.await().connect(req)
    }

    // Disconnect from VPN service.
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        deferredNode.await().disconnect()
    }

    // Unlock identity and return it's address. Internally mobile node will create default identity
    // if it is not created yet.
    suspend fun unlockIdentity(): Identity = withContext(Dispatchers.IO) {
        val res = deferredNode.await().unlockIdentity()
        Identity(address = res.identityAddress)
    }

    // Get current identity registration status.
    suspend fun getIdentityRegistrationStatus(identityAddress: String) = withContext(Dispatchers.IO) {
        val req = GetIdentityRegistrationStatusRequest()
        req.identityAddress = identityAddress
        val res = deferredNode.await().getIdentityRegistrationStatus(req)
        IdentityRegistrationStatus(value = res.status)
    }

    // Get registration fees.
    suspend fun getIdentityRegistrationFees() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().identityRegistrationFees
        IdentityRegistrationFees(fee = res.fee)
    }

    // Register identity with given fee.
    suspend fun registerIdentity(identityAddress: String, fee: Long) = withContext(Dispatchers.IO) {
        val req = RegisterIdentityRequest()
        req.identityAddress = identityAddress
        req.fee = fee
        deferredNode.await().registerIdentity(req)
    }

    // Top-up balance with myst tokens.
    suspend fun topUpBalance(identityAddress: String) = withContext(Dispatchers.IO) {
        val req = TopUpRequest()
        req.identityAddress = identityAddress
        deferredNode.await().topUp(req)
    }

    // Get current credit balance.
    suspend fun getBalance() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().balance
        Balance(value = res.balance)
    }

    // Get current location with country and IP.
    suspend fun getLocation() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().location
        Location(
                ip = res.ip,
                countryCode = res.country
        )
    }

    // Get current  connection status.
    suspend fun getStatus() = withContext(Dispatchers.IO) {
        val res = deferredNode.await().status
        Status(
                state = res.state,
                providerID = res.providerID,
                serviceType = res.serviceType
        )
    }

    // Send user feedback.
    suspend fun sendFeedback(description: String) = withContext(Dispatchers.IO) {
        val req = SendFeedbackRequest()
        req.description = description
        deferredNode.await().sendFeedback(req)
    }

    private suspend fun getProposals(req: GetProposalsRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().getProposals(req)
    }

    private suspend fun getProposal(req: GetProposalRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().getProposal(req)
    }

    private suspend fun parseProposals(bytes: ByteArray) = withContext(Dispatchers.Default) {
        Klaxon().parse<ProposalsResponse>(bytes.inputStream())
    }

    private suspend fun parseProposal(bytes: ByteArray) = withContext(Dispatchers.Default) {
        Klaxon().parse<ProposalResponse>(bytes.inputStream())
    }
}
