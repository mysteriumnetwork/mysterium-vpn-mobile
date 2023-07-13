package network.mysterium.provider.ui.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.sentry.Sentry
import network.mysterium.provider.ui.navigation.AppNavigation
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.MysteriumTheme
import network.mysterium.provider.ui.theme.Styles

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        Log.e("TAG", data.toString())
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
