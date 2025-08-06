package network.mysterium.provider.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import network.mysterium.node.analytics.NodeAnalytics
import network.mysterium.node.analytics.event.AnalyticsEvent
import network.mysterium.provider.ui.navigation.AppNavigation
import network.mysterium.provider.ui.theme.MysteriumTheme
import network.mysterium.provider.ui.theme.Styles
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val analytics by inject<NodeAnalytics>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        analytics.trackEvent(AnalyticsEvent.AppLaunchEvent)
        setContent {
            MysteriumTheme(this) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Styles.background),
                    color = Color.Transparent,
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
