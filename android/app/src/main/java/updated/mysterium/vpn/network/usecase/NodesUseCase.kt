package updated.mysterium.vpn.network.usecase

import mysterium.GetProposalsRequest
import network.mysterium.service.core.NodeRepository
import network.mysterium.vpn.R
import updated.mysterium.vpn.database.dao.NodeDao
import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.model.manual.connect.ProposalModel

class NodesUseCase(
    private val nodeRepository: NodeRepository,
    private val nodeDao: NodeDao
) {

    private companion object {
        const val SERVICE_TYPE = "wireguard"
        const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }

    suspend fun getAllInitialNodes(): List<NodeEntity> {
        val proposalsRequest = GetProposalsRequest().apply {
            this.refresh = refresh
            includeFailed = true
            serviceType = SERVICE_TYPE
        }
        return nodeRepository.proposals(proposalsRequest)
            .map { NodeEntity(it) }
    }

    suspend fun saveAllInitialNodes(nodesList: List<NodeEntity>) {
        nodeDao.apply {
            deleteAllUnsaved()
            insertAll(nodesList)
        }
    }

    suspend fun getAllSavedCountries() = mapNodesToCountriesGroups(nodeDao.getAllNodes())

    suspend fun getFavourites() = createProposalList(nodeDao.getFavourites())

    suspend fun deleteFromFavourite(proposalModel: ProposalModel) {
        nodeDao.deleteFromFavourite(proposalModel.id)
    }

    private fun mapNodesToCountriesGroups(allNodesList: List<NodeEntity>): List<CountryNodesModel> {
        val proposalList = createProposalList(allNodesList)
        return groupListByCountries(proposalList).sortedByDescending { it.proposalList.size }
    }

    private fun createProposalList(allNodesList: List<NodeEntity>): List<ProposalModel> {
        val minPricePerByte = allNodesList.minOf { it.pricePerByte }
        val maxPricePerByte = allNodesList.maxOf { it.pricePerByte }
        val firstPriceBorder = ((maxPricePerByte - minPricePerByte) / 3) + minPricePerByte
        val secondPriceBorder = (((maxPricePerByte - minPricePerByte) / 3) * 2) + minPricePerByte
        return allNodesList.map {
            ProposalModel(it).apply {
                calculatePriceLevel(
                    minPricePerByte,
                    firstPriceBorder,
                    secondPriceBorder
                )
            }
        }
    }

    private fun groupListByCountries(proposalList: List<ProposalModel>): List<CountryNodesModel> {
        val countryNodesList = mutableListOf<CountryNodesModel>()
        countryNodesList.add(
            index = 0,
            element = CountryNodesModel(
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
                    val allNodesByCountry = mutableListOf<ProposalModel>()
                    proposalList.forEach {
                        if (it.countryCode == node.countryCode) {
                            allNodesByCountry.add(it)
                        }
                    }
                    countryNodesList.add(
                        CountryNodesModel(
                            countryCode = node.countryCode,
                            countryName = node.countryName,
                            proposalList = allNodesByCountry.toList()
                        )
                    )
                }
            }
        return countryNodesList.toList()
    }
}
