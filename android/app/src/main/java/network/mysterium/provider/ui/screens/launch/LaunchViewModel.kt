package network.mysterium.provider.ui.screens.launch

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.node.Node
import network.mysterium.provider.R
import network.mysterium.provider.core.CoreViewModel
import network.mysterium.provider.ui.navigation.NavigationDestination

class LaunchViewModel(
    private val node: Node
) : CoreViewModel<Launch.Event, Launch.State, Launch.Effect>() {

    private companion object {
        val TAG: String = LaunchViewModel::class.java.simpleName
    }

    init {
        setEffect { Launch.Effect.RequestVpnPermission }
    }

    override fun createInitialState(): Launch.State {
        return Launch.State(error = null)
    }

    override fun handleEvent(event: Launch.Event) {
        when (event) {
            Launch.Event.InitializeNode -> {
                initializeNode()
            }
            Launch.Event.DeclinedVpnPermission -> {
                setState { copy(error = Launch.InitError(R.string.missing_vpn_permission)) }
            }
            Launch.Event.ConfirmedInitError -> {
                setEffect { Launch.Effect.CloseApp }
            }
        }
    }

    private fun initializeNode() = viewModelScope.launch {
        try {
            node.start()
            if (node.identity.value.isRegistered) {
                setEffect { Launch.Effect.Navigation(NavigationDestination.Home) }
            } else {
                setEffect { Launch.Effect.Navigation(NavigationDestination.Start) }
            }
        } catch (error: Throwable) {
            Log.e(TAG, "unable to init node", error)
            setState { copy(error = Launch.InitError(R.string.unable_init_node)) }
        }
    }
}
