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
        const val ALL_NODES_FILTER_ID = 0
    }

    val proposals: LiveData<List<CountryNodes>>
        get() = _proposals

    val filteredProposal: LiveData<List<Proposal>>
        get() = _filteredProposal

    private val _proposals = MutableLiveData<List<CountryNodes>>()
    private val _filteredProposal = MutableLiveData<List<Proposal>>()
    private val nodesUseCase = useCaseProvider.nodes()
    private val filterUseCase = useCaseProvider.filters()
    private var cachedNodesList: List<CountryNodes> = emptyList()
    private val handler = Handler()
    private val runnable = object : Runnable {

        override fun run() {
            try {
                getProposals()
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

    fun filterNodes(filterId: Int, countryCode: String): LiveData<Result<List<CountryNodes>>> {
        val allCountryNodes = cachedNodesList.find { countryNodes ->
            countryNodes.countryCode == countryCode
        }?.proposalList ?: emptyList()
        return mapNodesByFilterAndCountry(filterId, allCountryNodes)
    }

    private fun mapNodesByFilterAndCountry(filterId: Int, proposals: List<Proposal>) =
        liveDataResult {
            if (filterId == ALL_NODES_FILTER_ID) {
                _filteredProposal.postValue(proposals)
                mapProposalsToCountryNodes(proposals)
            } else {
                val byPresetList = filterUseCase.getProposalsByFilterId(filterId)
                val commonProposals = emptyList<Proposal>().toMutableList()
                byPresetList?.forEach { nodeEntity ->
                    proposals.find { proposal ->
                        proposal.providerID == nodeEntity.providerID
                    }?.let { commonProposal ->
                        val elementWithSameId = commonProposals.find { proposal ->
                            proposal.providerID == commonProposal.providerID
                        }
                        if (elementWithSameId == null) {
                            commonProposals.add(commonProposal)
                        }
                    }
                }
                _filteredProposal.postValue(commonProposals)
                mapProposalsToCountryNodes(commonProposals)
            }
        }

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
                _proposals.postValue(cachedNodesList)
            }
        }
    }
}
