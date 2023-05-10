package network.mystrium.node

import network.mysterium.terms.Terms
import network.mystrium.node.model.NodeTerms

internal class MysteriumMobileNode : MobileNode {

    override val terms: NodeTerms
        get() = NodeTerms(Terms.endUserMD(), Terms.version())

}
