package network.mysterium.provider.ui.components.buttons

import androidx.compose.runtime.Composable
import network.mysterium.provider.R

@Composable
fun BackButton(onClick: () -> Unit) {
    PrimaryIconButton(
        iconResId = R.drawable.ic_back,
        onClick = onClick
    )
}

@Composable
fun HomeButton(onClick: () -> Unit) {
    PrimaryIconButton(
        iconResId = R.drawable.ic_home,
        onClick = onClick
    )
}

@Composable
fun SettingsButton(onClick: () -> Unit) {
    PrimaryIconButton(
        iconResId = R.drawable.ic_settings,
        onClick = onClick
    )
}

@Composable
fun HelpButton(onClick: () -> Unit) {
    PrimaryIconButton(
        iconResId = R.drawable.ic_help,
        onClick = onClick
    )
}
