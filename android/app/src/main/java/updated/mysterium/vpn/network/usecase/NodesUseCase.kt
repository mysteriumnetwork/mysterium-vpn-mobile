package updated.mysterium.vpn.network.usecase

import mysterium.GetProposalsRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository
import network.mysterium.vpn.R
import updated.mysterium.vpn.database.dao.NodeDao
import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.model.manual.connect.CountryNodes
import updated.mysterium.vpn.model.manual.connect.Proposal

class NodesUseCase(
    private val nodeRepository: NodeRepository,
    private val nodeDao: NodeDao
) {

    private companion object {
        const val SERVICE_TYPE = "wireguard"
        const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun getAllProposals() = createProposalList(getAllNodes())

    suspend fun getAllCountries() = mapNodesToCountriesGroups(getAllNodes())

    suspend fun getFavourites() = checkFavouriteRelevance(
        createProposalList(nodeDao.getFavourites())
    )

    suspend fun addToFavourite(
        proposal: Proposal
    ) = nodeDao.addToFavourite(NodeEntity(proposal, true))

    suspend fun deleteFromFavourite(proposal: Proposal) {
        nodeDao.deleteFromFavourite(proposal.id)
    }

    suspend fun isFavourite(nodeId: String): NodeEntity? = nodeDao.getById(nodeId)

    private suspend fun getAllNodes(): List<NodeEntity> {
        val proposalsRequest = GetProposalsRequest().apply {
            this.refresh = refresh
            includeFailed = true
            serviceType = SERVICE_TYPE
        }
        return nodeRepository.proposals(proposalsRequest)
            .map { NodeEntity(it) }
    }

    private fun mapNodesToCountriesGroups(allNodesList: List<NodeEntity>): List<CountryNodes> {
        val proposalList = createProposalList(allNodesList)
        return groupListByCountries(proposalList).sortedByDescending { it.proposalList.size }
    }

    private fun createProposalList(allNodesList: List<NodeEntity>) = allNodesList.map {
        Proposal(it)
    }

    private fun groupListByCountries(proposalList: List<Proposal>): List<CountryNodes> {
        val countryNodesList = mutableListOf<CountryNodes>()
        countryNodesList.add(
            index = 0,
            element = CountryNodes(
                countryCode = ALL_COUNTRY_CODE,
                countryName = "",
                countryFlagRes = R.drawable.icon_all_countries,
                proposalList = proposalList
            )
        )
        proposalList.filter { it.countryCode != "" }
            .forEach { node ->
                val currentCountry = countryNodesList.find { it.countryCode == node.countryCode }
                if (currentCountry == null) {
                    val allNodesByCountry = mutableListOf<Proposal>()
                    proposalList.forEach {
                        if (it.countryCode == node.countryCode) {
                            allNodesByCountry.add(it)
                        }
                    }
                    countryNodesList.add(
                        CountryNodes(
                            countryCode = node.countryCode,
                            countryName = node.countryName,
                            proposalList = allNodesByCountry.toList()
                        )
                    )
                }
            }
        return countryNodesList.toList()
    }

    private suspend fun checkFavouriteRelevance(favourites: List<Proposal>): List<Proposal> {
        val allAvailableNodes = getAllNodes()
        favourites.forEach { favourite ->
            val nodeEntity = allAvailableNodes.find { node ->
                node.providerID == favourite.providerID
            }
            if (nodeEntity == null) {
                favourite.isAvailable = false
            }
        }
        return favourites
    }
}
