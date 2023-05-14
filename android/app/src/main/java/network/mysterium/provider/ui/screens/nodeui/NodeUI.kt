package network.mysterium.provider.ui.screens.nodeui

import network.mysterium.provider.core.UIEffect
import network.mysterium.provider.core.UIEvent
import network.mysterium.provider.core.UIState

sealed class NodeUI {
    sealed class Event : UIEvent {
        object Load : Event()
    }

    data class State(
        val url: String,
        val isRegistered: Boolean
    ) : UIState

    sealed class Effect : UIEffect
}