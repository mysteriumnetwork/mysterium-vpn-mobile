package updated.mysterium.vpn.model.proposal.parameters

enum class ProposalSortType(val type: Int) {
    COUNTRY(0),
    QUALITY(1);

    companion object {
        fun parse(type: Int): ProposalSortType {
            if (type == 0) {
                return COUNTRY
            }
            return QUALITY
        }
    }
}
