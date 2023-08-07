package network.mysterium.node.core

import mysterium.MobileNode

//workaround to inject mobile node
data class NodeContainer(val mobileNode: MobileNode?, val error: Throwable? = null)
