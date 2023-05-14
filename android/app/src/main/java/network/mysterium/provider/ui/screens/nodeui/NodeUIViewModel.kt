package network.mysterium.provider.ui.screens.nodeui

import network.mysterium.node.Node
import network.mysterium.provider.core.CoreViewModel

class NodeUIViewModel(
    private val node: Node
) : CoreViewModel<NodeUI.Event, NodeUI.State, NodeUI.Effect>() {
    override fun createInitialState(): NodeUI.State {
        return NodeUI.State(url = "")
    }

    init {
        setEvent(NodeUI.Event.Load)
    }

    override fun handleEvent(event: NodeUI.Event) {
        when (event) {
            NodeUI.Event.Load -> {
                setState { copy(url = node.nodeUIUrl) }
            }
        }
    }
}