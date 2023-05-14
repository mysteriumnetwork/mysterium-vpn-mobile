package network.mysterium.provider.ui.screens.nodeui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import network.mysterium.node.Node
import network.mysterium.provider.core.CoreViewModel

class NodeUIViewModel(
    private val node: Node
) : CoreViewModel<NodeUI.Event, NodeUI.State, NodeUI.Effect>() {

    init {
        setEvent(NodeUI.Event.Load)
        observeIdentity()
    }

    override fun createInitialState(): NodeUI.State {
        return NodeUI.State(
            url = "",
            isRegistered = false
        )
    }

    override fun handleEvent(event: NodeUI.Event) {
        when (event) {
            NodeUI.Event.Load -> {
                setState { copy(url = node.nodeUIUrl) }
            }
        }
    }

    private fun observeIdentity() = viewModelScope.launch {
        node.identity.collect {
            setState { copy(isRegistered = it.isRegistered) }
        }
    }
}