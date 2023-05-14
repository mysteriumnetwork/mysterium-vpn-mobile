package network.mysterium.provider.ui.screens.home

import network.mysterium.node.model.NodeServiceType
import network.mysterium.provider.core.UIEffect
import network.mysterium.provider.core.UIEvent
import network.mysterium.provider.core.UIState

sealed class Home {
    sealed class Event : UIEvent {
        object Load : Event()
    }

    data class State(
        val services: List<NodeServiceType>,
        val isLimitReached: Boolean,
        val balance: Double
    ) : UIState

    sealed class Effect : UIEffect
}