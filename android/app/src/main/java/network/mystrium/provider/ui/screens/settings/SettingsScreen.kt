package network.mystrium.provider.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import network.mystrium.provider.R
import network.mystrium.provider.ui.components.buttons.BackButton
import network.mystrium.provider.ui.components.content.RoundedBox
import network.mystrium.provider.ui.components.content.ScreenContent
import network.mystrium.provider.ui.components.switcher.Switcher
import network.mystrium.provider.ui.navigation.NavigationDestination
import network.mystrium.provider.ui.theme.Colors
import network.mystrium.provider.ui.theme.MysteriumTheme
import network.mystrium.provider.ui.theme.Paddings
import network.mystrium.provider.ui.theme.TextStyles
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    isOnboarding: Boolean,
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    SettingsContent(
        state = state,
        isOnboarding = isOnboarding,
        onEvent = { viewModel.setEvent(it) },
        onNavigate = onNavigate
    )
}

@Composable
private fun SettingsContent(
    state: Settings.State,
    isOnboarding: Boolean,
    onEvent: (Settings.Event) -> Unit,
    onNavigate: (NavigationDestination) -> Unit
) {
    ScreenContent(
        modifier = Modifier.padding(Paddings.default),
        title = stringResource(id = R.string.mobile_data_settings),
        color = Colors.secondaryBg,
        navLeading = {
            if (isOnboarding) {
                BackButton {
                    onNavigate(NavigationDestination.PopBack)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Paddings.default)
        ) {
            UseMobileData(checked = state.isMobileDataOn) {
                onEvent(Settings.Event.ToggleMobileData(it))
            }
            SetMobileDataLimit(checked = state.isMobileDataLimitOn) {
                onEvent(Settings.Event.ToggleLimit(it))
            }
        }
    }
}

@Composable
private fun UseMobileData(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    RoundedBox(
        modifier = Modifier.fillMaxWidth()
    ) {
        SwitchOption(
            title = stringResource(id = R.string.use_mobile_data),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SetMobileDataLimit(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    RoundedBox(
        modifier = Modifier.fillMaxWidth()
    ) {
        SwitchOption(
            title = stringResource(id = R.string.set_mobile_limit),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SwitchOption(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row {
        Text(
            text = title,
            style = TextStyles.body
        )
        Spacer(modifier = Modifier.weight(1f))
        Switcher(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    MysteriumTheme {
        SettingsContent(
            state = Settings.State(
                isMobileDataOn = true,
                isMobileDataLimitOn = false,
            ),
            isOnboarding = true,
            onEvent = {},
            onNavigate = {}
        )
    }
}