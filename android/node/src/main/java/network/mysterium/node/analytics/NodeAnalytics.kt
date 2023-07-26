package network.mysterium.node.analytics

import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import network.mysterium.node.analytics.event.AnalyticsEvent
import network.mysterium.node.utils.toPairs

interface NodeAnalytics {

    fun trackEvent(event: AnalyticsEvent)

}

class NodeAnalyticsImpl(private val analytics: FirebaseAnalytics) : NodeAnalytics {
    override fun trackEvent(event: AnalyticsEvent) {
        analytics
            .logEvent(event.name, bundleOf(*event.properties.toPairs()))
    }

}
