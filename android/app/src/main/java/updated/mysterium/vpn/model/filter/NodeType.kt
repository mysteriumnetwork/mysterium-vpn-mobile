package updated.mysterium.vpn.model.filter

typealias FullNodeType = network.mysterium.proposal.NodeType

enum class NodeType {
    ALL,
    RESIDENTIAL,
    NON_RESIDENTIAL;

    companion object {

        fun from(fullNodeType: FullNodeType) = when (fullNodeType) {
            FullNodeType.RESIDENTIAL -> RESIDENTIAL
            FullNodeType.CELLULAR -> RESIDENTIAL
            else -> NON_RESIDENTIAL
        }

        fun from(nodeType: String) = when (nodeType) {
            "residential" -> RESIDENTIAL
            "cellular" -> RESIDENTIAL
            else -> NON_RESIDENTIAL
        }
    }
}
