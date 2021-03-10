package updated.mysterium.vpn.ui.manual.connect.select.node.saved

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SavedNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()

    fun getSavedNodes() = liveDataResult {
        nodesUseCase.getFavourites()
    }

    fun deleteNodeFromFavourite(proposal: Proposal) = liveDataResult {
        nodesUseCase.deleteFromFavourite(proposal)
    }
}
