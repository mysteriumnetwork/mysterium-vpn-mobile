package updated.mysterium.vpn.ui.manual.connect.select.node.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.ProposalModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SavedNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()

    fun getSavedNodes() = liveDataResult {
        nodesUseCase.getFavourites()
    }

    fun deleteNodeFromFavourite(proposalModel: ProposalModel) = liveDataResult {
        nodesUseCase.deleteFromFavourite(proposalModel)
    }
}
