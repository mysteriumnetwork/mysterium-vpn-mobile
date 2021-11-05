package updated.mysterium.vpn.ui.base

import android.os.Handler
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
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.network.usecase.FilterUseCase

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "AllNodesViewModel"
        const val REQUEST_INTERVAL = 1000 * 60L // 1 minute
    }

    val proposals: LiveData<List<CountryNodes>>
        get() = _proposals


    val initialDataLoaded: LiveData<Boolean>
        get() = _initialDataLoaded

    private var allProposalsLoaded = false
    private var filtersLoaded = false
    private val _proposals = MutableLiveData<List<CountryNodes>>()
    private val _initialDataLoaded = MutableLiveData<Boolean>()
    private val nodesUseCase = useCaseProvider.nodes()
    private val filterUseCase = useCaseProvider.filters()
    private var cachedNodesList: List<CountryNodes> = emptyList()
    private val handler = Handler()
    private val allFiltersLists = emptyList<List<CountryNodes>?>().toMutableList()
    private val runnable = object : Runnable {

        override fun run() {
            try {
                getProposals()
                loadFilters()
            } catch (exception: Exception) {
                Log.e(TAG, exception.localizedMessage ?: exception.toString())
            } finally {
                handler.postDelayed(this, REQUEST_INTERVAL)
            }
        }
    }

    fun launchProposalsPeriodically() {
        // fetch new proposals list every 60 sec
        handler.post(runnable)
    }

    fun stopPeriodicalProposalFetch() {
        handler.removeCallbacks(runnable)
    }

    fun getFilteredListById(filterId: Int) = liveDataResult {
        val allNodes = proposals.value?.first()?.proposalList ?: emptyList()
        if (filterId == FilterUseCase.ALL_NODES_FILTER_ID) {
            mapProposalsToCountryNodes(allNodes)
        } else {
            allFiltersLists[filterId] ?: emptyList()
        }
    }

    private fun mapProposalsToCountryNodes(
        proposals: List<Proposal>
    ) = nodesUseCase.groupListByCountries(proposals)

    private fun getProposals(isReload: Boolean = false) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
            if (!isReload) { // try to re load list only one time
                getProposals(isReload = true)
            }
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            cachedNodesList = nodesUseCase.getAllCountries()
            if (cachedNodesList.isNotEmpty()) {
                _proposals.postValue(cachedNodesList)
            }
            allProposalsLoaded = true
            if (filtersLoaded) {
                _initialDataLoaded.postValue(true)
            }
        }
    }

    private fun loadFilters() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            nodesUseCase.getAllCountries()
            val allFilters = filterUseCase.getSystemPresets()
            allFilters.forEachIndexed { index, presetFilter ->
                if (allFiltersLists.size > index) {
                    val proposalsList = filterUseCase.getProposalsByFilterId(
                        presetFilter.filterId
                    )
                    allFiltersLists[index] = nodesUseCase.mapNodesToCountriesGroups(
                        proposalsList ?: emptyList()
                    )
                } else {
                    val proposalsList = filterUseCase.getProposalsByFilterId(
                        presetFilter.filterId
                    )
                    allFiltersLists.add(
                        nodesUseCase.mapNodesToCountriesGroups(proposalsList ?: emptyList())
                    )
                }
                if (index + 1 == allFilters.size) {
                    filtersLoaded = true
                    if (allProposalsLoaded) {
                        _initialDataLoaded.postValue(true)
                    }
                }
            }
        }
    }
}
