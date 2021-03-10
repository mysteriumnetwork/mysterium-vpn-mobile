package updated.mysterium.vpn.ui.manual.connect.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.model.filter.NodeFilter
import updated.mysterium.vpn.model.filter.NodePrice
import updated.mysterium.vpn.model.filter.NodeQuality
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.model.manual.connect.CountryNodes
import updated.mysterium.vpn.model.manual.connect.PriceLevel
import updated.mysterium.vpn.model.manual.connect.Proposal

class FilterViewModel : ViewModel() {

    val proposalsList: LiveData<List<Proposal>>
        get() = _proposalsList

    lateinit var countryNodes: CountryNodes
    private val _proposalsList = MutableLiveData<List<Proposal>>()

    fun applyInitialFilter(nodes: CountryNodes, nodeFilter: NodeFilter) {
        countryNodes = nodes
        countryNodes.proposalList.sortedBy {
            it.payment.rate.perBytes
        }.forEachIndexed { index, proposalModel ->
            when {
                proposalModel.payment.rate.perBytes == 0.0 -> {
                    proposalModel.priceLevel = PriceLevel.FREE
                }
                index <= countryNodes.proposalList.size * 0.3 -> {
                    proposalModel.priceLevel = PriceLevel.LOW
                }
                index >= countryNodes.proposalList.size * 0.7 -> {
                    proposalModel.priceLevel = PriceLevel.HIGH
                }
                else -> {
                    proposalModel.priceLevel = PriceLevel.MEDIUM
                }
            }
        }
        _proposalsList.value = getFilteredProposalList(nodeFilter)
    }

    fun filterList(nodeFilter: NodeFilter) {
        _proposalsList.value = getFilteredProposalList(nodeFilter)
    }

    private fun getFilteredProposalList(nodeFilter: NodeFilter) = countryNodes.proposalList
        .filter {
            filterByType(it, nodeFilter.typeFilter)
        }.filter {
            filterByPrice(it, nodeFilter.priceFilter)
        }.filter {
            filterByQuality(it, nodeFilter.qualityFilter)
        }

    private fun filterByType(proposal: Proposal, nodeType: NodeType): Boolean {
        return if (nodeType == NodeType.ALL) {
            true
        } else {
            NodeType.from(proposal.nodeType) == nodeType
        }
    }

    private fun filterByPrice(proposal: Proposal, nodePrice: NodePrice): Boolean {
        val currentNodePrice = proposal.priceLevel
        return if (nodePrice == NodePrice.HIGH || currentNodePrice == PriceLevel.FREE) {
            true
        } else {
            NodePrice.from(currentNodePrice) == nodePrice
        }
    }

    private fun filterByQuality(proposal: Proposal, nodeQuality: NodeQuality): Boolean {
        val currentNodeQuality = NodeQuality.from(proposal.qualityLevel)
        return when (nodeQuality) {
            NodeQuality.LOW -> true
            NodeQuality.MEDIUM -> {
                currentNodeQuality == NodeQuality.MEDIUM || currentNodeQuality == NodeQuality.HIGH
            }
            NodeQuality.HIGH -> {
                currentNodeQuality == NodeQuality.HIGH
            }
        }
    }
}
