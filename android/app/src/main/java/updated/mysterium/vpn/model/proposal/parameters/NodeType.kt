package updated.mysterium.vpn.model.proposal.parameters

enum class NodeType(val nodeType: String) {
    ALL("all"),
    BUSINESS("business"),
    CELLULAR("cellular"),
    HOSTING("hosting"),
    RESIDENTIAL("residential");

    companion object {
        fun parse(nodeType: String): NodeType {
            return values().find { it.nodeType == nodeType } ?: ALL
        }
    }
}
