package network.mystrium.provider.ui.screens.launch

import network.mystrium.provider.core.UIEffect
import network.mystrium.provider.core.UIEvent
import network.mystrium.provider.core.UIState
import network.mystrium.provider.ui.navigation.NavigationDestination

sealed class Launch {
    sealed class Event : UIEvent {
    }

    data class State(
        val isLoading: Boolean
    ) : UIState

    sealed class Effect : UIEffect {
        data class Navigation(val destination: NavigationDestination) : Effect()
    }
}
