package network.mystrium.node

import network.mysterium.terms.Terms

internal class MysteriumMobileNode : MobileNode {

    override val terms: String
        get() = Terms.endUserMD()

}
