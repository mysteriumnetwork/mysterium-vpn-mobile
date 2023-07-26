package network.mysterium.provider.ui.screens.start

import androidx.lifecycle.ViewModel
import network.mysterium.node.analytics.NodeAnalytics
import network.mysterium.node.analytics.event.AnalyticsEvent

//todo if needed replace with CoreViewModel
class StartScreenViewModel(private val analytics: NodeAnalytics) : ViewModel() {

    fun trackOnboardClick() {
        analytics.trackEvent(AnalyticsEvent.OnboardingButtonPressEvent)
    }
}
