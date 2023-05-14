package network.mysterium.provider.ui.screens.settings

import network.mysterium.node.Node
import network.mysterium.provider.core.CoreViewModel
import network.mysterium.provider.ui.navigation.NavigationDestination

class SettingsViewModel(
    private val node: Node
) : CoreViewModel<Settings.Event, Settings.State, Settings.Effect>() {

    private companion object {
        const val MIN_LIMIT = 50
        const val MAX_LIMIT = 999999
        val TAG: String = SettingsViewModel::class.java.simpleName
    }

    init {
        setEvent(Settings.Event.FetchConfig)
    }

    override fun createInitialState(): Settings.State {
        return Settings.State(
            isMobileDataOn = false,
            isMobileDataLimitOn = false,
            isAllowUseOnBatteryOn = false,
            mobileDataLimit = null,
            mobileDataLimitInvalid = false,
            isSaveButtonEnabled = false,
            isStartingNode = false,
            nodeError = null
        )
    }

    override fun handleEvent(event: Settings.Event) {
        when (event) {
            Settings.Event.FetchConfig -> {
                val config = node.config
                setState {
                    copy(
                        isMobileDataOn = config.useMobileData,
                        isMobileDataLimitOn = config.useMobileDataLimit,
                        mobileDataLimit = config.mobileDataLimit
                    )
                }
            }
            is Settings.Event.ToggleMobileData -> {
                node.config = node.config.copy(useMobileData = event.checked)
                setState { copy(isMobileDataOn = event.checked) }
            }

            is Settings.Event.ToggleLimit -> {
                node.config = node.config.copy(useMobileDataLimit = event.checked)
                setState { copy(isMobileDataLimitOn = event.checked) }
            }

            is Settings.Event.ToggleAllowUseOnBattery -> {
                setState { copy(isAllowUseOnBatteryOn = event.checked) }
            }

            is Settings.Event.UpdateLimit -> {
                processLimitInput(event.value)
            }
            Settings.Event.SaveMobileDataLimit -> {
                val limit = currentState.mobileDataLimit ?: return
                node.config = node.config.copy(mobileDataLimit = limit)
                setState { copy(isSaveButtonEnabled = false) }
            }
            Settings.Event.OnContinue -> {
                startNodeInForeground()
            }
        }
    }

    private fun processLimitInput(value: String) {
        if (value.isEmpty()) {
            setState {
                copy(
                    mobileDataLimit = value.toIntOrNull(),
                    mobileDataLimitInvalid = true,
                    isSaveButtonEnabled = false
                )
            }
            return
        }

        val limit = value.toIntOrNull() ?: return
        val isValid = limit in MIN_LIMIT..MAX_LIMIT
        val isSaveEnabled = node.config.mobileDataLimit?.let {
            isValid && limit != it
        } ?: isValid

        setState {
            copy(
                mobileDataLimit = limit,
                mobileDataLimitInvalid = !isValid,
                isSaveButtonEnabled = isSaveEnabled
            )
        }
    }

    private fun startNodeInForeground() {
        node.enableForeground()
        setEffect { Settings.Effect.Navigation(NavigationDestination.NodeUI(true)) }
    }
}