package updated.mysterium.vpn.ui.manual.connect.select.node.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.CountryNodes
import updated.mysterium.vpn.model.manual.connect.SortType
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val proposals: LiveData<List<CountryNodes>>
        get() = _proposals

    private val _proposals = MutableLiveData<List<CountryNodes>>()
    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<CountryNodes> = emptyList()
    private var isLoaded = false

    fun initProposals() {
        viewModelScope.launch(Dispatchers.IO) {
            cachedNodesList = nodesUseCase.getAllCountries()
            isLoaded = true
        }
    }

    fun getProposals() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!isLoaded) {
                cachedNodesList = nodesUseCase.getAllCountries()
            }
            _proposals.postValue(cachedNodesList)
        }
    }

    fun getSortedProposal(sortType: SortType) = liveDataResult {
        if (sortType == SortType.NODES) {
            cachedNodesList.sortedByDescending { it.proposalList.size }
        } else {
            cachedNodesList.sortedBy { it.countryName }
        }
    }
}
