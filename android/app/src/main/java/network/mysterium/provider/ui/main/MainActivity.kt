package network.mysterium.provider.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import network.mysterium.node.analytics.NodeAnalytics
import network.mysterium.node.analytics.event.AnalyticsEvent
import network.mysterium.provider.ui.navigation.AppNavigation
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.MysteriumTheme
import network.mysterium.provider.ui.theme.Styles
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val analytics by inject<NodeAnalytics>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics.trackEvent(AnalyticsEvent.AppLaunchEvent)
        setContent {
            MysteriumTheme {
                Surface(
                    modifier = Modifier.background(Styles.background),
                    color = Colors.statusBar
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
