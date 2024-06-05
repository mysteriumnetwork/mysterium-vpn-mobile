package network.mysterium.provider.ui.theme

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import network.mysterium.provider.R

private val colorScheme = lightColorScheme(
    primary = Colors.primary
)

@Composable
fun MysteriumTheme(
    context: Context,
    content: @Composable () -> Unit,
) {
    val colorScheme = colorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val background = ContextCompat.getDrawable(context, R.drawable.gradient_theme)
            val window = (view.context as Activity).window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
                navigationBarColor = ContextCompat.getColor(context, android.R.color.transparent)
                setBackgroundDrawable(background)
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
