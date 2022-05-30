package updated.mysterium.vpn.ui.favourites

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class FavouritesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val favouritesUseCase = useCaseProvider.favourites()
    private var allNodes: List<Proposal> = emptyList()

    fun getSavedNodes(proposals: List<Proposal>?) = liveDataResult {
        if (proposals == null) {
            favouritesUseCase.getFavourites(allNodes)
        } else {
            allNodes = proposals
            favouritesUseCase.getFavourites(proposals)
        }
    }

    fun deleteNodeFromFavourite(proposal: Proposal) = liveDataResult {
        favouritesUseCase.deleteFromFavourite(proposal)
    }
}
