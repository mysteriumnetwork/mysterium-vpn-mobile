package updated.mysterium.vpn.network.usecase

import mysterium.GetProposalsRequest
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.model.manual.connect.PriceLevel
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.nodes.ProposalItem
import java.util.*

class NodesUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    companion object {
        const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
        private const val SERVICE_TYPE = "wireguard"
        private const val NAT_COMPATIBILITY = "auto"
    }

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun getProposalList(): List<Proposal> {
        val allNodes = requestProposals().map { NodeEntity(it) }
        return createProposalList(allNodes)
    }

    suspend fun getFilteredProposals(
        filterId: Int? = null,
        countryCode: String? = null
    ): List<Proposal> {
        return requestProposals(filterId, countryCode).map { Proposal(NodeEntity(it)) }
    }

    private suspend fun requestProposals(
        filterId: Int? = null,
        countryCode: String? = null
    ): List<ProposalItem> {
        val request = GetProposalsRequest().apply {
            refresh = true
            serviceType = SERVICE_TYPE
            natCompatibility = getNatCompatibility()
        }
        filterId?.let {
            request.presetID = filterId.toLong()
        }
        countryCode?.let {
            request.locationCountry = countryCode.toUpperCase(Locale.ROOT)
        }
        return nodeRepository.proposals(request)
    }

    private fun createProposalList(allNodesList: List<NodeEntity>): List<Proposal> {
        val parsedProposals = allNodesList.map {
            Proposal(it)
        }
        val sortedByPriceNodes = ArrayList(allNodesList)
        sortedByPriceNodes.sortedBy {
            it.pricePerByte
        }.forEachIndexed { index, node ->
            if (index < parsedProposals.size) {
                when {
                    index <= (sortedByPriceNodes.size * 0.33) -> {
                        parsedProposals.find {
                            it.providerID == node.providerID
                        }?.priceLevel = PriceLevel.LOW
                    }
                    index <= (sortedByPriceNodes.size * 0.66) -> {
                        parsedProposals.find {
                            it.providerID == node.providerID
                        }?.priceLevel = PriceLevel.MEDIUM
                    }
                    else -> {
                        parsedProposals.find {
                            it.providerID == node.providerID
                        }?.priceLevel = PriceLevel.HIGH
                    }
                }
            }
        }
        return parsedProposals
    }

    private fun getNatCompatibility(): String {
        val isNatAvailable = sharedPreferencesManager.getBoolPreferenceValue(
            SharedPreferencesList.IS_NAT_AVAILABLE, true
        )
        return if (isNatAvailable) {
            NAT_COMPATIBILITY
        } else {
            ""
        }
    }

}
