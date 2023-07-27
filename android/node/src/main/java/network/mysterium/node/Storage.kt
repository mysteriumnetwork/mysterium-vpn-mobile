package network.mysterium.node

import network.mysterium.node.model.NodeConfig
import network.mysterium.node.model.NodeUsage

interface Storage {
    var isRegistered: Boolean
    var config: NodeConfig
    var usage: NodeUsage
}
