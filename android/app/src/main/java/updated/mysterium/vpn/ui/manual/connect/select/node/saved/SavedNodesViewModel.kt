package updated.mysterium.vpn.ui.manual.connect.select.node.saved

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SavedNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()
    private var allNodes: List<Proposal> = emptyList()

    fun getSavedNodes(proposals: List<Proposal>?) = liveDataResult {
        if (proposals == null) {
            nodesUseCase.getFavourites(allNodes)
        } else {
            allNodes = proposals
            nodesUseCase.getFavourites(proposals)
        }
    }

    fun deleteNodeFromFavourite(proposal: Proposal) = liveDataResult {
        nodesUseCase.deleteFromFavourite(proposal)
    }
}
