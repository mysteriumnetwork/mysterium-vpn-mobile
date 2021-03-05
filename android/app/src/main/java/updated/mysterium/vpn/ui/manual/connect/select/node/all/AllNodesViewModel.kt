package updated.mysterium.vpn.ui.manual.connect.select.node.all

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.model.manual.connect.SortType
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<CountryNodesModel> = emptyList()

    fun getInitialProposals() = liveDataResult {
        cachedNodesList = nodesUseCase.getAllCountries()
        cachedNodesList
    }

    fun getSortedProposal(sortType: SortType) = liveDataResult {
        if (sortType == SortType.NODES) {
            cachedNodesList.sortedByDescending { it.proposalList.size }
        } else {
            cachedNodesList.sortedBy { it.countryName }
        }
    }
}
