package network.mysterium.node

import network.mysterium.node.model.NodeConfig

internal interface Storage {
    var isRegistered: Boolean
    var config: NodeConfig
}