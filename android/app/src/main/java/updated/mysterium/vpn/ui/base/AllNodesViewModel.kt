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
import updated.mysterium.vpn.model.manual.connect.CountryInfo
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.network.usecase.FilterUseCase
import updated.mysterium.vpn.network.usecase.NodesUseCase.Companion.ALL_COUNTRY_CODE

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "AllNodesViewModel"
        const val REQUEST_INTERVAL = 1000 * 60L // 1 minute
    }

    val proposals: LiveData<List<Proposal>>
        get() = _proposals

    val initialDataLoaded: LiveData<Boolean>
        get() = _initialDataLoaded

    private var allCountryInfoLoaded = false
    private var filteredCountryInfoLoaded = false
    private val countryInfoList = MutableLiveData<List<CountryInfo>>()
    private val _proposals = MutableLiveData<List<Proposal>>()
    private val _initialDataLoaded = MutableLiveData<Boolean>()
    private val nodesUseCase = useCaseProvider.nodes()
    private val filterUseCase = useCaseProvider.filters()
    private var cachedCountryInfoList: List<CountryInfo> = emptyList()
    private var cachedProposalList: List<Proposal> = emptyList()
    private val handler = Handler()
    private val filteredCountryInfoLists = emptyList<List<CountryInfo>?>().toMutableList()
    private val runnable = object : Runnable {

        override fun run() {
            try {
                loadCountryInfoList()
                loadFilteredCountryInfo()
                loadProposalList()
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

    fun getCountryInfoListWithFilter(filterId: Int) = liveDataResult {
        if (filterId == FilterUseCase.ALL_NODES_FILTER_ID) {
            countryInfoList.value ?: emptyList()
        } else {
            filteredCountryInfoLists[filterId] ?: emptyList()
        }
    }

    fun getProposalsWithFilter(filterId: Int) = liveDataResult {
        if (filterId == FilterUseCase.ALL_NODES_FILTER_ID) {
            proposals.value
        } else {
            filterUseCase.getProposalsByFilterId(filterId)
        } ?: emptyList()
    }

    fun getProposalsWithFilterAndCountry(filterId: Int, countryCode: String) = liveDataResult {
        if (filterId == FilterUseCase.ALL_NODES_FILTER_ID && countryCode == ALL_COUNTRY_CODE) {
            proposals.value
        } else {
            filterUseCase.getProposalsWithFilterAndCountry(filterId, countryCode)
        } ?: emptyList()
    }

    private fun loadCountryInfoList(isReload: Boolean = false) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
            if (!isReload) { // try to re load list only one time
                loadCountryInfoList(isReload = true)
            }
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            cachedCountryInfoList = nodesUseCase.getCountryInfoList()
            if (cachedCountryInfoList.isNotEmpty()) {
                countryInfoList.postValue(cachedCountryInfoList)
            }
            allCountryInfoLoaded = true
            if (filteredCountryInfoLoaded) {
                _initialDataLoaded.postValue(true)
            }
        }
    }

    private fun loadFilteredCountryInfo() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            val filters = filterUseCase.getSystemPresets()
            filters.forEachIndexed { index, filter ->
                if (filteredCountryInfoLists.size > index) {
                    filteredCountryInfoLists[index] =
                        filterUseCase.getCountryInfoListByFilterId(filter.filterId) ?: emptyList()
                } else {
                    filteredCountryInfoLists.add(
                        filterUseCase.getCountryInfoListByFilterId(filter.filterId) ?: emptyList()
                    )
                }
                if (index + 1 == filters.size) {
                    filteredCountryInfoLoaded = true
                    if (allCountryInfoLoaded) {
                        _initialDataLoaded.postValue(true)
                    }
                }
            }
        }
    }

    private fun loadProposalList(isReload: Boolean = false) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
            if (!isReload) { // try to re load list only one time
                loadProposalList(isReload = true)
            }
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            cachedProposalList = nodesUseCase.getAllProposals()
            if (cachedProposalList.isNotEmpty()) {
                _proposals.postValue(cachedProposalList)
            }
        }
    }

}
