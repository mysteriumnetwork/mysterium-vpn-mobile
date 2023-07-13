package network.mysterium.provider.ui.screens.nodeui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
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
    private val node: Node,
    private val context: Context,
) : CoreViewModel<NodeUI.Event, NodeUI.State, NodeUI.Effect>() {

    init {
        setEvent(NodeUI.Event.Load)
        observeIdentity()
    }

    override fun createInitialState(): NodeUI.State {
        return NodeUI.State(
            url = "",
            reload = {},
            isRegistered = false,
            ignoredUrls = listOf(Config.mystNodesUrl)
        )
    }

    override fun handleEvent(event: NodeUI.Event) {
        when (event) {
            NodeUI.Event.Load -> {
                setState { copy(url = node.nodeUIUrl) }
            }

            is NodeUI.Event.UrlLoaded -> {
                if (!event.isIgnored) {
                    handleUrl(event.url)
                } else {
                    handleIgnoredUrl(event.url)
                }
            }

            is NodeUI.Event.SetReloadCallback -> {
                setState { copy(reload = event.reload) }
            }
        }
    }


    //for now we will just open in browser all ignored urls, but may be modification if future
    private fun handleIgnoredUrl(url: Uri) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = url
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
        )
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
