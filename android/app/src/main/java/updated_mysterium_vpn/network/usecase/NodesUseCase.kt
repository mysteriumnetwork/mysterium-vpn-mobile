package updated_mysterium_vpn.network.usecase

import mysterium.GetProposalsRequest
import network.mysterium.proposal.ProposalViewItem
import network.mysterium.service.core.NodeRepository
import network.mysterium.vpn.R
import updated_mysterium_vpn.model.manual_select.CountryNodesModel

class NodesUseCase(private val nodeRepository: NodeRepository) {

    suspend fun getAllProposalByNodes() = allNodesToCountries(getAllNodes())

    private suspend fun getAllNodes(): List<ProposalViewItem> {
        val proposalsRequest = GetProposalsRequest().apply {
            this.refresh = refresh
            includeFailed = true
            serviceType = SERVICE_TYPE
        }
        return nodeRepository.proposals(proposalsRequest)
                .filter { it.countryCode != "" }
                .map { ProposalViewItem.parse(it) }
    }

    private fun allNodesToCountries(allNodesList: List<ProposalViewItem>): List<CountryNodesModel> {
        val countryNodesList = mutableListOf<CountryNodesModel>()
        allNodesList.forEach { node ->
            val currentCountry = countryNodesList.find { it.countryCode == node.countryCode }
            if (currentCountry == null) {
                countryNodesList.add(
                        CountryNodesModel(
                                countryCode = node.countryCode,
                                countryName = node.countryName,
                                nodesList = mutableListOf(node)
                        )
                )
            } else {
                currentCountry.nodesList.add(node)
            }
        }
        countryNodesList.sortByDescending { it.nodesList.size }
        countryNodesList.add(0, ALL_COUNTRY_NODE.copy(nodesList = allNodesList.toMutableList()))
        return countryNodesList
    }

    companion object {
        private const val SERVICE_TYPE = "wireguard"
        private const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
        private val ALL_COUNTRY_NODE = CountryNodesModel(
                countryCode = ALL_COUNTRY_CODE,
                countryName = "",
                countryFlagRes = R.drawable.icon_all_countries,
                nodesList = emptyList<ProposalViewItem>().toMutableList()
        )
    }
}
