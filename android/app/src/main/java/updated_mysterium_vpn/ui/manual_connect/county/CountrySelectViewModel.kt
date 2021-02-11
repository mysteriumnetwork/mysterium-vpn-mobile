package updated_mysterium_vpn.ui.manual_connect.county

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import updated_mysterium_vpn.common.extensions.liveDataResult
import updated_mysterium_vpn.model.manual_select.CountryNodesModel
import updated_mysterium_vpn.model.manual_select.SortType
import updated_mysterium_vpn.network.provider.usecase.UseCaseProvider

class CountrySelectViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<CountryNodesModel> = emptyList()
    private var sortType = SortType.NODE

    fun getInitialProposals(): LiveData<Result<List<CountryNodesModel>>> = liveDataResult {
        cachedNodesList = nodesUseCase.getAllProposalByNodes()
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
            sortedList.sortByDescending { it.nodesList.size }
            sortedList
        }
    }
}
