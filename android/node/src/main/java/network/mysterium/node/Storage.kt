package network.mysterium.node

import network.mysterium.node.model.NodeRunnerConfig

internal interface Storage {
    var isRegistered: Boolean
    var config: NodeRunnerConfig
}