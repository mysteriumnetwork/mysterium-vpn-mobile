package updated.mysterium.vpn.ui.nodes.list

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class FilterViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val filterUseCase = useCaseProvider.filters()

    fun getProposals(filterId: Int, proposals: List<Proposal>) = liveDataResult {
        val byPresetList = filterUseCase.getProposalsByFilterId(filterId)
        val commonProposals = emptyList<Proposal>().toMutableList()
        byPresetList.forEach { nodeEntity ->
            proposals.find { proposal ->
                proposal.providerID == nodeEntity.providerID
            }?.let { commonProposal ->
                commonProposals.add(commonProposal)
            }
        }
        commonProposals
    }
}
