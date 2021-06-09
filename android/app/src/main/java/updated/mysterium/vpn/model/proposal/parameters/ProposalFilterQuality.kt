package updated.mysterium.vpn.model.proposal.parameters

data class ProposalFilterQuality(
    var level: QualityLevel,
    var qualityIncludeUnreachable: Boolean,
    val proposalsCount: Int = 0
)
