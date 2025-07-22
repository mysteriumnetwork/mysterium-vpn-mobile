package updated.mysterium.vpn.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.model.manual.connect.Proposal
import java.util.*

class SearchViewModel : ViewModel() {

    val searchResult: LiveData<List<Proposal>>
        get() = _searchResult

    private val _searchResult = MutableLiveData<List<Proposal>>()
    private var allProposal = emptyList<Proposal>()

    fun setAllNodes(proposals: List<Proposal>) {
        allProposal = proposals
    }

    fun search(text: String) {
        if (text.isNotEmpty()) {
            val resultByCountry = allProposal.filter {
                it.countryName
                    .lowercase(Locale.ROOT)
                    .contains(text.lowercase(Locale.ROOT))
            }
            val resultByProviderId = allProposal.filter {
                it.providerID
                    .lowercase(Locale.ROOT)
                    .contains(text.lowercase(Locale.ROOT))
            }
            _searchResult.value = resultByCountry + resultByProviderId
        } else {
            _searchResult.value = emptyList()
        }
    }
}
