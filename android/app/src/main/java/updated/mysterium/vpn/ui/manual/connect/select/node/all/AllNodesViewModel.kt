package updated.mysterium.vpn.ui.manual.connect.select.node.all

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.CountryNodes
import updated.mysterium.vpn.model.manual.connect.SortType
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "AllNodesViewModel"
    }

    val proposals: LiveData<List<CountryNodes>>
        get() = _proposals

    private val _proposals = MutableLiveData<List<CountryNodes>>()
    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<CountryNodes> = emptyList()

    fun initProposals() {
        viewModelScope.launch(Dispatchers.IO) {
            cachedNodesList = nodesUseCase.getAllCountries()
        }
    }

    fun getProposals() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            updateLiveData()
            cachedNodesList = nodesUseCase.getAllCountries()
            updateLiveData()
        }
    }

    fun getSortedProposal(sortType: SortType) = liveDataResult {
        if (sortType == SortType.NODES) {
            cachedNodesList.sortedByDescending { it.proposalList.size }
        } else {
            cachedNodesList.sortedBy { it.countryName }
        }
    }

    private fun updateLiveData() {
        if (cachedNodesList.isNotEmpty()) {
            _proposals.postValue(cachedNodesList)
        }
    }
}
