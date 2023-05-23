package network.mysterium.provider.ui.screens.nodeui

import android.net.Uri
import network.mysterium.provider.core.UIEffect
import network.mysterium.provider.core.UIEvent
import network.mysterium.provider.core.UIState

sealed class NodeUI {
    sealed class Event : UIEvent {
        object Load : Event()
        data class SetReloadCallback(val reload: () -> Unit): Event()
        data class UrlLoaded(val url: Uri): Event()
    }

    data class State(
        val url: String,
        val reload: () -> Unit,
        val isRegistered: Boolean
    ) : UIState

    sealed class Effect : UIEffect
}
