package updated_mysterium_vpn.model.manual_connect

data class CountryNodesModel(
        val countryFlagRes: Int? = null,
        val countryCode: String,
        val countryName: String,
        val proposalList: MutableList<ProposalModel>
)
