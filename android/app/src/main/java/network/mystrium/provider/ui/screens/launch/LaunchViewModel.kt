package network.mystrium.provider.ui.screens.launch

import network.mystrium.provider.core.CoreViewModel
import network.mystrium.provider.ui.navigation.NavigationDestination

class LaunchViewModel : CoreViewModel<Launch.Event, Launch.State, Launch.Effect>() {

    override fun createInitialState(): Launch.State {
        return Launch.State(false)
    }

    init {
        setEffect { Launch.Effect.Navigation(NavigationDestination.Start) }
    }

    override fun handleEvent(event: Launch.Event) {

    }
}
