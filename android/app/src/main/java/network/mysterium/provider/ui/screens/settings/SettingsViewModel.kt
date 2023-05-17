package network.mysterium.provider.ui.screens.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.node.Node
import network.mysterium.node.model.NodeConfig
import network.mysterium.provider.Config
import network.mysterium.provider.core.CoreViewModel
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.utils.Converter

class SettingsViewModel(
    private val node: Node
) : CoreViewModel<Settings.Event, Settings.State, Settings.Effect>() {

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
            showShutDownConfirmation = false,
            continueButtonEnabled = true,
            nodeError = null,
        )
    }

    override fun handleEvent(event: Settings.Event) {
        when (event) {
            Settings.Event.FetchConfig -> {
                val config = node.config
                val continueEnabled = if (config.useMobileDataLimit) {
                    config.mobileDataLimit != null
                } else {
                    true
                }
                setState {
                    copy(
                        isMobileDataOn = config.useMobileData,
                        isMobileDataLimitOn = config.useMobileDataLimit,
                        isAllowUseOnBatteryOn = config.allowUseOnBattery,
                        continueButtonEnabled = continueEnabled,
                        mobileDataLimit = config.mobileDataLimit?.let {
                            Converter.bytesToMegabytes(it)
                        }
                    )
                }
            }
            is Settings.Event.ToggleMobileData -> {
                updateNodeConfig(node.config.copy(useMobileData = event.checked))
                setState { copy(isMobileDataOn = event.checked) }
            }
            is Settings.Event.ToggleLimit -> {
                val continueEnabled = if (event.checked) {
                    node.config.mobileDataLimit != null
                } else {
                    true
                }
                updateNodeConfig(node.config.copy(useMobileDataLimit = event.checked))
                setState {
                    copy(
                        isMobileDataLimitOn = event.checked,
                        continueButtonEnabled = continueEnabled
                    )
                }
            }
            is Settings.Event.ToggleAllowUseOnBattery -> {
                updateNodeConfig(node.config.copy(allowUseOnBattery = event.checked))
                setState { copy(isAllowUseOnBatteryOn = event.checked) }
            }

            is Settings.Event.UpdateLimit -> {
                processLimitInput(event.value)
            }
            Settings.Event.SaveMobileDataLimit -> {
                val limit = currentState.mobileDataLimit
                if (limit == null) {
                    setState { copy(continueButtonEnabled = false) }
                    return
                }
                updateNodeConfig(node.config.copy(mobileDataLimit = Converter.megabytesToBytes(limit)))
                setState {
                    copy(
                        isSaveButtonEnabled = false,
                        continueButtonEnabled = true
                    )
                }
            }
            Settings.Event.OnContinue -> {
                startNodeInForeground()
            }
            Settings.Event.ShutDown -> {
                setState { copy(showShutDownConfirmation = true) }
            }
            Settings.Event.ConfirmShutDown -> {
                shutDownNode()
            }
            Settings.Event.CancelShutDown -> {
                setState { copy(showShutDownConfirmation = false) }
            }
        }
    }

    private fun processLimitInput(value: String) {
        if (value.isEmpty()) {
            setState {
                copy(
                    mobileDataLimit = value.toLongOrNull(),
                    mobileDataLimitInvalid = true,
                    isSaveButtonEnabled = false
                )
            }
            return
        }

        val limit = value.toLongOrNull() ?: return
        val limitBytes = Converter.megabytesToBytes(limit)
        val isValid = limit in Config.minMobileDataLimit..Config.maxMobileDataLimit
        val isSaveEnabled = node.config.mobileDataLimit?.let {
            isValid && limitBytes != it
        } ?: isValid

        setState {
            copy(
                mobileDataLimit = limit,
                mobileDataLimitInvalid = !isValid,
                isSaveButtonEnabled = isSaveEnabled
            )
        }
    }

    private fun startNodeInForeground() = viewModelScope.launch(Dispatchers.IO) {
        setState { copy(isStartingNode = true) }
        node.enableForegroundService()
        setState { copy(isStartingNode = false) }
        setEffect { Settings.Effect.Navigation(NavigationDestination.NodeUI(true)) }
    }

    private fun updateNodeConfig(config: NodeConfig) = viewModelScope.launch {
        node.updateConfig(config)
    }

    private fun shutDownNode() = viewModelScope.launch {
        node.stop()
        setEffect { Settings.Effect.CloseApp }
    }
}