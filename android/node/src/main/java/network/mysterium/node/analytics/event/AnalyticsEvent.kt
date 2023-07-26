package network.mysterium.node.analytics.event

sealed class AnalyticsEvent(open val name: String) {
    open val properties: Map<String, Any?> = emptyMap()

    sealed class ToggleAnalyticsEvent(override val name: String) :
        AnalyticsEvent(name) {

        abstract val isEnabled: Boolean
        override val properties: Map<String, Any?>
            get() = super.properties + mapOf("is_enabled" to isEnabled)


        data class NodeUiState(override val isEnabled: Boolean) :
            ToggleAnalyticsEvent("node_status")

        data class UseMobileDataEvent(
            override val isEnabled: Boolean,
        ) : ToggleAnalyticsEvent(name = "use_mobile_data")

        data class UseOnBatteryEvent(override val isEnabled: Boolean) :
            ToggleAnalyticsEvent(name = "use_on_battery")

        data class UseMobileDataLimitUsageEvent(override val isEnabled: Boolean, val limit: Long?) :
            ToggleAnalyticsEvent(name = "mobile_data_usage_limit") {
            override val properties: Map<String, Any?>
                get() = super.properties + mapOf("mobile_data_limit" to limit)
        }
    }

    object AppLaunchEvent : AnalyticsEvent(name = "app_launch")
    object OnboardingButtonPressEvent : AnalyticsEvent(name = "onboarding_button_pressed")
    object OnboardingCompletedPressEvent : AnalyticsEvent(name = "onboarding_completed")
    object HelpButtonPressedEvent : AnalyticsEvent(name = "help_button_pressed")
    object NodeUiOpenedEvent : AnalyticsEvent(name = "nodeui_opened")

    data class ServicesStatusEvent(val serviceName: String, val status: String) :
        AnalyticsEvent(name = "service_status") {

        override val properties: Map<String, Any?>
            get() = super.properties + mapOf(
                "service_name" to serviceName,
                "status" to status,
            )

    }

}


