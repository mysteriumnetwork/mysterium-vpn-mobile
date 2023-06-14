package network.mysterium.provider.ui.screens.nodeui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import network.mysterium.node.Node
import network.mysterium.node.model.NodeIdentity
import network.mysterium.provider.Config
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
            reload = {},
            isRegistered = false
        )
    }

    override fun handleEvent(event: NodeUI.Event) {
        when (event) {
            NodeUI.Event.Load -> {
                setState { copy(url = node.nodeUIUrl) }
            }
            is NodeUI.Event.UrlLoaded -> {
                handleUrl(event.url)
            }

            is NodeUI.Event.SetReloadCallback -> {
                setState { copy(reload = event.reload) }
            }
        }
    }

    private fun observeIdentity() = viewModelScope.launch {
        node.identity.collect {
            setState { copy(isRegistered = it.status == NodeIdentity.Status.REGISTERED) }
        }
    }

    private fun handleUrl(url: Uri) {
        val path = url.toString()
        Log.d("NodeUI", "Url loaded: $path")
        if (path.startsWith(Config.stripeRedirectUrl) || path.startsWith(Config.payPalRedirectUrl)) {
            viewModelScope.launch {
                delay(3000)
                currentState.reload()
            }
        }
    }
}
