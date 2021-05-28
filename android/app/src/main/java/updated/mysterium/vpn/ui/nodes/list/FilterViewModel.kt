package updated.mysterium.vpn.ui.nodes.list

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.PresetFilter
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.network.usecase.FilterUseCase

class FilterViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val ALL_NODES_FILTER_ID = 0
    }

    var countryCode: String? = null
    var filter: PresetFilter? = null
    var cacheProposals: List<Proposal>? = null
        private set

    private val filterUseCase = useCaseProvider.filters()

    fun getProposals(filterId: Int, proposals: List<Proposal>) = liveDataResult {
        if (filterId == ALL_NODES_FILTER_ID) {
            cacheProposals = proposals
            proposals
        } else {
            val byPresetList = filterUseCase.getProposalsByFilterId(filterId)
            val commonProposals = emptyList<Proposal>().toMutableList()
            byPresetList?.forEach { nodeEntity ->
                proposals.find { proposal ->
                    proposal.providerID == nodeEntity.providerID
                }?.let { commonProposal ->
                    commonProposals.add(commonProposal)
                }
            }
            cacheProposals = commonProposals
            commonProposals
        }
    }
}
