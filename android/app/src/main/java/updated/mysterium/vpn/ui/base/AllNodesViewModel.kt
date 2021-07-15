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

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "AllNodesViewModel"
        const val REQUEST_INTERVAL = 1000 * 60L // 1 minute
//        const val ALL_NODES_FILTER_ID = 0
    }

    val proposals: LiveData<List<CountryNodes>>
        get() = _proposals

//    val filteredProposal: LiveData<List<Proposal>>
//        get() = _filteredProposal

    val filterLoaded: LiveData<Boolean>
        get() = _filterLoaded

    private val _proposals = MutableLiveData<List<CountryNodes>>()

    //    private val _filteredProposal = MutableLiveData<List<Proposal>>()
    private val _filterLoaded = MutableLiveData<Boolean>()
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
        Log.i(TAG, "getFilteredListById $filterId")
        val allNodes = proposals.value?.first()?.proposalList ?: emptyList()
        if (allFiltersLists[filterId] == null) {
            mapProposalsToCountryNodes(allNodes)
        } else {
            allFiltersLists[filterId] ?: emptyList()
        }
    }

//    //    fun filterNodes(filterId: Int, countryCode: String): LiveData<Result<List<CountryNodes>>> {
////        val allCountryNodes = cachedNodesList.find { countryNodes ->
////            countryNodes.countryCode == countryCode
////        }?.proposalList ?: emptyList()
////        return mapNodesByFilterAndCountry(filterId, allCountryNodes)
////    }
////
//    private suspend fun mapNodesByFilterAndCountry(filterId: Int, proposals: List<Proposal>) {
//        if (filterId == ALL_NODES_FILTER_ID) {
//            mapProposalsToCountryNodes(proposals)
//        } else {
//            val byPresetList = filterUseCase.getProposalsByFilterId(filterId)
//            val commonProposals = emptyList<Proposal>().toMutableList()
//            byPresetList?.forEach { nodeEntity ->
//                proposals.find { proposal ->
//                    proposal.providerID == nodeEntity.providerID
//                }?.let { commonProposal ->
//                    val elementWithSameId = commonProposals.find { proposal ->
//                        proposal.providerID == commonProposal.providerID
//                    }
//                    if (elementWithSameId == null) {
//                        commonProposals.add(commonProposal)
//                    }
//                }
//            }
//            _filteredProposal.postValue(commonProposals)
//            mapProposalsToCountryNodes(commonProposals)
//        }
//    }

    private suspend fun mapProposalsToCountryNodes(
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
                Log.i(TAG, "Load all proposals")
                _proposals.postValue(cachedNodesList)
            }
        }
    }

    private fun loadFilters() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        Log.i(TAG, "Try load all filters")
        viewModelScope.launch(Dispatchers.IO + handler) {
            val allFilters = filterUseCase.getSystemPresets()
            Log.i(TAG, "Size = ${allFilters.size}")
            allFilters.forEachIndexed { index, presetFilter ->
                Log.i(TAG, "Start load filter $index")
                if (allFiltersLists.size > index) {
                    val proposalsList = filterUseCase.getProposalsByFilterId(
                        presetFilter.filterId
                    )
                    Log.i(TAG, "$index size = ${proposalsList?.size}")
                    allFiltersLists[index] = nodesUseCase.mapNodesToCountriesGroups(
                        proposalsList ?: emptyList()
                    )
                } else {
                    val proposalsList = filterUseCase.getProposalsByFilterId(
                        presetFilter.filterId
                    )
                    Log.i(TAG, "$index size = ${proposalsList?.size}")
                    allFiltersLists.add(
                        nodesUseCase.mapNodesToCountriesGroups(proposalsList ?: emptyList())
                    )
                }
                Log.i(TAG, "Finish load filter $index")
                if (index + 1 == allFilters.size) {
                    Log.i(TAG, "Filters loaded")
                    _filterLoaded.postValue(true)
                }
            }
        }
    }
}
