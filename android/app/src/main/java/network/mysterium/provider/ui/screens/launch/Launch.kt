package network.mysterium.provider.ui.screens.launch

import androidx.annotation.StringRes
import network.mysterium.provider.core.UIEffect
import network.mysterium.provider.core.UIEvent
import network.mysterium.provider.core.UIState
import network.mysterium.provider.ui.navigation.NavigationDestination

sealed class Launch {
    sealed class Event : UIEvent {
        object InitializeNode : Event()
        object DeclinedVpnPermission : Event()
        object ConfirmedInitError : Event()
        object RequestNotificationPermission : Event()
    }

    data class State(
        val error: InitError?
    ) : UIState

    data class InitError(
        @StringRes val messageResId: Int
    )

    sealed class Effect : UIEffect {
        object RequestVpnPermission : Effect()
        object RequestNotificationPermission : Effect()
        object CloseApp : Effect()
        data class Navigation(val destination: NavigationDestination) : Effect()
    }
}
