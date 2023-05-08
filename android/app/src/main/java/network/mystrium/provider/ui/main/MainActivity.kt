package network.mystrium.provider.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import network.mystrium.provider.ui.navigation.AppNavigation
import network.mystrium.provider.ui.screens.start.StartScreen
import network.mystrium.provider.ui.theme.Colors
import network.mystrium.provider.ui.theme.MysteriumTheme
import network.mystrium.provider.ui.theme.Styles

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
