package updated.mysterium.vpn.ui.manual.connect.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import updated.mysterium.vpn.model.filter.NodeFilter
import updated.mysterium.vpn.model.filter.NodePrice
import updated.mysterium.vpn.model.filter.NodeQuality
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.model.manual.connect.PriceLevel
import updated.mysterium.vpn.model.manual.connect.ProposalModel

class FilterViewModel : ViewModel() {

    private var _proposalsList = MutableLiveData<List<ProposalModel>>()
    val proposalsList
        get() = _proposalsList

    lateinit var nodesModel: CountryNodesModel

    fun applyInitialFilter(countryNodesModel: CountryNodesModel, nodeFilter: NodeFilter) {
        nodesModel = countryNodesModel
        _proposalsList.value = getFilteredProposalList(nodeFilter)
    }

    fun filterList(nodeFilter: NodeFilter) {
        _proposalsList.value = getFilteredProposalList(nodeFilter)
    }

    private fun getFilteredProposalList(nodeFilter: NodeFilter) = nodesModel.proposalList
        .filter {
            filterByType(it, nodeFilter.typeFilter)
        }.filter {
            filterByPrice(it, nodeFilter.priceFilter)
        }.filter {
            filterByQuality(it, nodeFilter.qualityFilter)
        }

    private fun filterByType(proposalModel: ProposalModel, nodeType: NodeType): Boolean {
        return if (nodeType == NodeType.ALL) {
            true
        } else {
            NodeType.from(proposalModel.nodeType) == nodeType
        }
    }

    private fun filterByPrice(proposalModel: ProposalModel, nodePrice: NodePrice): Boolean {
        val currentNodePrice = proposalModel.priceLevel
        return if (nodePrice == NodePrice.HIGH || currentNodePrice == PriceLevel.FREE) {
            true
        } else {
            NodePrice.from(currentNodePrice) == nodePrice
        }
    }

    private fun filterByQuality(proposalModel: ProposalModel, nodeQuality: NodeQuality): Boolean {
        val currentNodeQuality = NodeQuality.from(proposalModel.qualityLevel)
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
