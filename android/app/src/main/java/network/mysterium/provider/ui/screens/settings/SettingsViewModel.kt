package network.mysterium.provider.ui.screens.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import network.mysterium.node.Node
import network.mysterium.node.analytics.NodeAnalytics
import network.mysterium.node.analytics.event.AnalyticsEvent
import network.mysterium.node.model.NodeConfig
import network.mysterium.provider.Config
import network.mysterium.provider.core.CoreViewModel
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.utils.Converter

class SettingsViewModel(
    private val node: Node,
    private val analytics: NodeAnalytics,
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
            isLoading = false
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
                analytics.trackEvent(AnalyticsEvent.ToggleAnalyticsEvent.UseMobileDataEvent(event.checked))
                updateNodeConfig(node.config.copy(useMobileData = event.checked))
                setState { copy(isMobileDataOn = event.checked) }
            }

            is Settings.Event.ToggleLimit -> {
                val continueEnabled = if (event.checked) {
                    node.config.mobileDataLimit != null
                } else {
                    true
                }
                analytics.trackEvent(
                    AnalyticsEvent.ToggleAnalyticsEvent.UseMobileDataLimitUsageEvent(
                        event.checked,
                        limit = node.config.mobileDataLimit,
                    )
                )
                updateNodeConfig(node.config.copy(useMobileDataLimit = event.checked))
                setState {
                    copy(
                        isMobileDataLimitOn = event.checked,
                        continueButtonEnabled = continueEnabled
                    )
                }
            }

            is Settings.Event.ToggleAllowUseOnBattery -> {
                analytics.trackEvent(AnalyticsEvent.ToggleAnalyticsEvent.UseOnBatteryEvent(event.checked))
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
                val bytesLimit = Converter.megabytesToBytes(limit)
                analytics.trackEvent(
                    AnalyticsEvent.ToggleAnalyticsEvent.UseMobileDataLimitUsageEvent(
                        currentState.isMobileDataLimitOn,
                        limit = bytesLimit
                    )
                )
                updateNodeConfig(node.config.copy(mobileDataLimit = bytesLimit))
                setState {
                    copy(
                        isSaveButtonEnabled = false,
                        continueButtonEnabled = true
                    )
                }
            }

            Settings.Event.OnContinue -> {
                analytics.trackEvent(AnalyticsEvent.OnboardingCompletedPressEvent)
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

    private fun startNodeInForeground() = launch(dispatcher = Dispatchers.IO) {
        setState { copy(isStartingNode = true) }
        node.enableForegroundService()
        setState { copy(isStartingNode = false) }
        setEffect { Settings.Effect.Navigation(NavigationDestination.NodeUI(true)) }
    }

    private fun updateNodeConfig(config: NodeConfig) = launch {
        node.updateConfig(config)
    }

    private fun shutDownNode() = launch {
        setState { copy(isLoading = true, showShutDownConfirmation = false) }

        withContext(ioDispatcher) {
            node.stop()
        }
        setState { copy(isLoading = true) }
        setEffect { Settings.Effect.CloseApp }
    }

    fun trackHelpPressed() {
        analytics.trackEvent(AnalyticsEvent.HelpButtonPressedEvent)
    }
}
