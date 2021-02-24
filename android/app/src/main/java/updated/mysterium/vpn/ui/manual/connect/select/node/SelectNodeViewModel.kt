package updated.mysterium.vpn.ui.manual.connect.select.node

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.model.manual.connect.SortType
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SelectNodeViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<CountryNodesModel> = emptyList()
    private var sortType = SortType.NODE

    fun getInitialProposals(): LiveData<Result<List<CountryNodesModel>>> = liveDataResult {
        cachedNodesList = nodesUseCase.getAllSavedCountries()
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
            val sortedList: MutableList<CountryNodesModel> = cachedNodesList.toMutableList()
            sortedList.sortBy { it.countryName }
            sortedList
        } else {
            val sortedList: MutableList<CountryNodesModel> = cachedNodesList.toMutableList()
            sortedList.sortByDescending { it.proposalList.size }
            sortedList
        }
    }
}
