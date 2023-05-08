package network.mystrium.provider.ui.screens.settings

import network.mystrium.provider.core.CoreViewModel

class SettingsViewModel : CoreViewModel<Settings.Event, Settings.State, Settings.Effect>() {
    companion object {
        const val MIN_LIMIT = 50
        const val MAX_LIMIT = 999999
    }

    override fun createInitialState(): Settings.State {
        return Settings.State(
            isMobileDataOn = false,
            isMobileDataLimitOn = false,
            isAllowUseOnBatteryOn = false,
            mobileDataLimit = "",
            mobileDataLimitInvalid = false
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

            is Settings.Event.ToggleAllowUseOnBattery -> {
                setState { copy(isAllowUseOnBatteryOn = event.checked) }
            }

            is Settings.Event.UpdateLimit -> {
                processLimitInput(event.value)
            }
        }
    }

    private fun processLimitInput(value: String) {
        if (value.isEmpty()) {
            setState {
                copy(
                    mobileDataLimit = value,
                    mobileDataLimitInvalid = true
                )
            }
            return
        }

        val limit = value.toIntOrNull() ?: return
        val isValid = limit < MIN_LIMIT || limit > MAX_LIMIT
        setState {
            copy(
                mobileDataLimit = value,
                mobileDataLimitInvalid = isValid
            )
        }
    }
}