package network.mysterium.service.core

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mysterium.ConnectRequest
import mysterium.GetProposalRequest
import mysterium.GetProposalsRequest

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

class NodeRepository(private val deferredNode: DeferredNode) {

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

    suspend fun getProposal(providerID: String, serviceType: String): ProposalItem? {
        val req = GetProposalRequest()
        req.providerID = providerID
        req.serviceType = serviceType

        val bytes = getProposal(req)
        val proposalsResponse = parseProposal(bytes)
        return proposalsResponse?.proposal
    }

    suspend fun registerConnectionStatusChangeCallback(cb: (status: String) -> Unit) {
        deferredNode.await().registerConnectionStatusChangeCallback { status -> cb(status) }
    }

    suspend fun registerStatisticsChangeCallback(cb: (stats: Statistics) -> Unit) {
        deferredNode.await().registerStatisticsChangeCallback { duration, bytesReceived, bytesSent ->
            cb(Statistics(duration, bytesReceived, bytesSent))
        }
    }

    suspend fun connect(req: ConnectRequest) = withContext(Dispatchers.IO) {
        deferredNode.await().connect(req)
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        deferredNode.await().disconnect()
    }

    suspend fun unlockIdentity(): String = withContext(Dispatchers.IO) {
        deferredNode.await().unlockIdentity()
    }

    suspend fun getLocation(): Location {
        val location = getLocationAsync()
        return Location(
                ip = location.ip,
                countryCode = location.country
        )
    }

    suspend fun getStatus(): Status {
        val status = getStatusAsync()
        return Status(
                state = status.state,
                providerID = status.providerID,
                serviceType = status.serviceType
        )
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

    private suspend fun getLocationAsync() = withContext(Dispatchers.IO) {
        deferredNode.await().location
    }

    private suspend fun getStatusAsync() = withContext(Dispatchers.IO) {
        deferredNode.await().status
    }
}
