package updated.mysterium.vpn.ui.manual.connect.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import updated.mysterium.vpn.model.manual.connect.ProposalModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SearchViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val searchResult: LiveData<List<ProposalModel>>
        get() = _searchResult

    private val nodesUseCase = useCaseProvider.nodes()
    private val _searchResult = MutableLiveData<List<ProposalModel>>()
    private var allProposal = emptyList<ProposalModel>()

    init {
        viewModelScope.launch {
            allProposal = nodesUseCase.getAllProposals()
        }
    }

    fun search(text: String) {
        if (text.isNotEmpty()) {
            val resultByCountry = allProposal.filter {
                it.countryName.contains(text)
            }
            val resultByProviderId = allProposal.filter {
                it.providerID.contains(text)
            }
            _searchResult.value = resultByCountry + resultByProviderId
        } else {
            _searchResult.value = emptyList()
        }
    }
}
