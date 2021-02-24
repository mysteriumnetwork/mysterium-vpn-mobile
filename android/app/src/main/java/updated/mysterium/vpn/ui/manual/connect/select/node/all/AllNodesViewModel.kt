package updated.mysterium.vpn.ui.manual.connect.select.node.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.model.manual.connect.SortType
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<CountryNodesModel> = emptyList()
    private var sortType = SortType.NODE

    fun getInitialProposals(): LiveData<Result<List<CountryNodesModel>>> = liveDataResult {
        cachedNodesList = nodesUseCase.getAllCountries()
        cachedNodesList
    }

    fun changeSortType(): LiveData<Result<SortType>> = liveDataResult {
        sortType = if (sortType == SortType.NODE) {
            SortType.COUNTRIES
        } else {
            SortType.NODE
        }
        sortType
    }

    fun getSortedProposal(): LiveData<Result<List<CountryNodesModel>>> = liveDataResult {
        if (sortType == SortType.NODE) {
            cachedNodesList.sortedBy { it.countryName }
        } else {
            cachedNodesList.sortedByDescending { it.proposalList.size }
        }
    }
}
