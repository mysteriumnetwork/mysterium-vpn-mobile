package network.mystrium.provider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import network.mystrium.provider.ui.screens.start.StartScreen
import network.mystrium.provider.ui.theme.MysteriumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MysteriumTheme {
                StartScreen()
            }
        }
    }
}
