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

    private val nodeFilter = NodeFilter()
    lateinit var nodesModel: CountryNodesModel

    fun applyInitialFilter(countryNodesModel: CountryNodesModel) {
        nodesModel = countryNodesModel
        _proposalsList.value = getFilteredProposalList()
    }

    fun onNodeTypeClicked() = liveData<NodeType> {
        nodeFilter.onTypeChanged()
        _proposalsList.value = getFilteredProposalList()
        nodeFilter.typeFilter
    }

    fun onNodePriceClicked() = liveData<NodePrice> {
        nodeFilter.onPriceChanged()
        _proposalsList.value = getFilteredProposalList()
        nodeFilter.priceFilter
    }

    fun onNodeQualityClicked() = liveData<NodeQuality> {
        nodeFilter.onQualityChanged()
        _proposalsList.value = getFilteredProposalList()
        nodeFilter.qualityFilter
    }

    private fun getFilteredProposalList() = nodesModel.proposalList
        .filter {
            filterByType(it)
        }.filter {
            filterByPrice(it)
        }.filter {
            filterByQuality(it)
        }

    private fun filterByType(proposalModel: ProposalModel): Boolean {
        return if (nodeFilter.typeFilter == NodeType.ALL) {
            true
        } else {
            NodeType.from(proposalModel.nodeType) == nodeFilter.typeFilter
        }
    }

    private fun filterByPrice(proposalModel: ProposalModel): Boolean {
        val currentNodePrice = proposalModel.priceLevel
        return if (nodeFilter.priceFilter == NodePrice.HIGH || currentNodePrice == PriceLevel.FREE) {
            true
        } else {
            NodePrice.from(currentNodePrice) == nodeFilter.priceFilter
        }
    }

    private fun filterByQuality(proposalModel: ProposalModel): Boolean {
        val currentNodeQuality = NodeQuality.from(proposalModel.qualityLevel)
        return when (nodeFilter.qualityFilter) {
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
