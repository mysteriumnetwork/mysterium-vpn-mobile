package updated_mysterium_vpn.model.manual_select

import network.mysterium.proposal.ProposalViewItem

data class CountryNodesModel(
        val countryFlagRes: Int? = null,
        val countryCode: String,
        val countryName: String,
        val nodesList: MutableList<ProposalViewItem>
)
