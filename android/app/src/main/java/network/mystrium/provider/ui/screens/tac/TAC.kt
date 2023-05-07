package network.mystrium.provider.ui.screens.tac

import network.mystrium.provider.core.UIEffect
import network.mystrium.provider.core.UIEvent
import network.mystrium.provider.core.UIState

sealed class TAC {
    sealed class Event : UIEvent {
        object LoadTAC: Event()
    }

    data class State(val terms: String) : UIState {

    }

    sealed class Effect : UIEffect {
    }
}
