package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.database.dao.NodeDao
import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.model.manual.connect.PriceLevel
import updated.mysterium.vpn.model.manual.connect.Proposal
import java.util.ArrayList

class FavouritesUseCase(
    private val nodeDao: NodeDao
) {

    suspend fun getFavourites(proposals: List<Proposal>) = checkFavouriteRelevance(
        allAvailableNodes = proposals,
        favourites = createProposalList(nodeDao.getFavourites())
    )

    suspend fun addToFavourite(
        proposal: Proposal
    ) = nodeDao.addToFavourite(NodeEntity(proposal, true))

    suspend fun deleteFromFavourite(proposal: Proposal) {
        nodeDao.deleteFromFavourite(proposal.providerID + proposal.serviceType)
    }

    suspend fun isFavourite(nodeId: String): NodeEntity? = nodeDao.getById(nodeId)

    private fun checkFavouriteRelevance(
        allAvailableNodes: List<Proposal>,
        favourites: List<Proposal>
    ): List<Proposal> {
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

}
