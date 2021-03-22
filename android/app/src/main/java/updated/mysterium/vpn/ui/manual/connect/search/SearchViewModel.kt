package updated.mysterium.vpn.ui.manual.connect.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.*

class SearchViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val searchResult: LiveData<List<Proposal>>
        get() = _searchResult

    val initialDataLoaded: LiveData<Unit>
        get() = _initialDataLoaded

    private val nodesUseCase = useCaseProvider.nodes()
    private val _searchResult = MutableLiveData<List<Proposal>>()
    private val _initialDataLoaded = MutableLiveData<Unit>()
    private var allProposal = emptyList<Proposal>()

    init {
        viewModelScope.launch {
            allProposal = nodesUseCase.getAllProposals()
            _initialDataLoaded.postValue(Unit)
        }
    }

    fun search(text: String) {
        if (text.isNotEmpty()) {
            val resultByCountry = allProposal.filter {
                it.countryName
                    .toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT))
            }
            val resultByProviderId = allProposal.filter {
                it.providerID
                    .toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT))
            }
            _searchResult.value = resultByCountry + resultByProviderId
        } else {
            _searchResult.value = emptyList()
        }
    }
}
