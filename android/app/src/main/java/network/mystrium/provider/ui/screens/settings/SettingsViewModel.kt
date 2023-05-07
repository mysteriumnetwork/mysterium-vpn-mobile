package network.mystrium.provider.ui.screens.settings

import network.mystrium.provider.core.CoreViewModel

class SettingsViewModel : CoreViewModel<Settings.Event, Settings.State, Settings.Effect>() {
    override fun createInitialState(): Settings.State {
        return Settings.State(
            isMobileDataOn = false,
            isMobileDataLimitOn = false
        )
    }

    override fun handleEvent(event: Settings.Event) {
        when (event) {
            is Settings.Event.ToggleMobileData -> {
                setState { copy(isMobileDataOn = event.checked) }
            }
            is Settings.Event.ToggleLimit -> {
                setState { copy(isMobileDataLimitOn = event.checked) }
            }
        }
    }
}