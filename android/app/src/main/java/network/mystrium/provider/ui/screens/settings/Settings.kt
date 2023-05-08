package network.mystrium.provider.ui.screens.settings

import network.mystrium.provider.core.UIEffect
import network.mystrium.provider.core.UIEvent
import network.mystrium.provider.core.UIState

sealed class Settings {
    sealed class Event : UIEvent {
        data class ToggleMobileData(val checked: Boolean) : Event()
        data class ToggleLimit(val checked: Boolean) : Event()
        data class ToggleAllowUseOnBattery(val checked: Boolean) : Event()
        data class UpdateLimit(val value: String) : Event()
    }

    data class State(
        val isMobileDataOn: Boolean,
        val isMobileDataLimitOn: Boolean,
        val isAllowUseOnBatteryOn: Boolean,
        val mobileDataLimit: String,
        val mobileDataLimitInvalid: Boolean
    ) : UIState

    sealed class Effect : UIEffect {
    }
}