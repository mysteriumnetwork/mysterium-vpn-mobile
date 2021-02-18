package updated_mysterium_vpn.network.usecase

import mysterium.GetProposalsRequest
import network.mysterium.service.core.NodeRepository
import network.mysterium.vpn.R
import updated_mysterium_vpn.database.dao.NodeDao
import updated_mysterium_vpn.database.entity.NodeEntity
import updated_mysterium_vpn.model.manual_connect.CountryNodesModel
import updated_mysterium_vpn.model.manual_connect.ProposalModel

class NodesUseCase(
        private val nodeRepository: NodeRepository,
        private val nodeDao: NodeDao
) {

    suspend fun getAllInitialNodes(): List<NodeEntity> {
        val proposalsRequest = GetProposalsRequest().apply {
            this.refresh = refresh
            includeFailed = true
            serviceType = SERVICE_TYPE
        }
        return nodeRepository.proposals(proposalsRequest)
                .filter { it.countryCode != "" }
                .map { NodeEntity.createNodeFromProposal(it) }
    }

    suspend fun saveAllInitialNodes(nodesList: List<NodeEntity>) {
        nodeDao.apply {
            deleteAll()
            insertAll(nodesList)
        }
    }

    suspend fun getAllSavedCountries() = mapNodesToCountriesGroups(nodeDao.getAllNodes())

    private fun mapNodesToCountriesGroups(allNodesList: List<NodeEntity>): List<CountryNodesModel> {
        val proposalList = allNodesList.map { ProposalModel.createProposalFromNode(it) }
        val countryNodesList = mutableListOf<CountryNodesModel>()
        proposalList.forEach { node ->
            val currentCountry = countryNodesList.find { it.countryCode == node.countryCode }
            if (currentCountry == null) {
                countryNodesList.add(
                        CountryNodesModel(
                                countryCode = node.countryCode,
                                countryName = node.countryName,
                                proposalList = mutableListOf(node)
                        )
                )
            } else {
                currentCountry.proposalList.add(node)
            }
        }
        countryNodesList.sortByDescending { it.proposalList.size }
        countryNodesList.add(0, ALL_COUNTRY_NODE.copy(proposalList = proposalList.toMutableList()))
        return countryNodesList
    }

    companion object {
        private const val SERVICE_TYPE = "wireguard"
        private const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
        private val ALL_COUNTRY_NODE = CountryNodesModel(
                countryCode = ALL_COUNTRY_CODE,
                countryName = "",
                countryFlagRes = R.drawable.icon_all_countries,
                proposalList = emptyList<ProposalModel>().toMutableList()
        )
    }
}
