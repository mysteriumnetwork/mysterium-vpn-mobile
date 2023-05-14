package network.mysterium.provider.ui.screens.tac

import network.mysterium.node.model.NodeTerms
import network.mysterium.provider.core.UIEffect
import network.mysterium.provider.core.UIEvent
import network.mysterium.provider.core.UIState

sealed class TAC {
    sealed class Event : UIEvent {
        object LoadTAC : Event()
    }

    data class State(
        val terms: NodeTerms
    ) : UIState

    sealed class Effect : UIEffect {
    }
}
