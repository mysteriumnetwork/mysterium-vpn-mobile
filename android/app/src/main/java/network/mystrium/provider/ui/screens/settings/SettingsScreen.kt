package network.mystrium.provider.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mystrium.provider.Config
import network.mystrium.provider.R
import network.mystrium.provider.ui.components.buttons.BackButton
import network.mystrium.provider.ui.components.buttons.PrimaryButton
import network.mystrium.provider.ui.components.content.ScreenContent
import network.mystrium.provider.ui.components.input.InputTextField
import network.mystrium.provider.ui.navigation.NavigationDestination
import network.mystrium.provider.ui.screens.settings.views.ButtonOption
import network.mystrium.provider.ui.screens.settings.views.SwitchOption
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
        Column {
            OptionsContent(
                modifier = Modifier.weight(1f),
                state = state,
                isOnboarding = isOnboarding,
                onEvent = onEvent
            )

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Paddings.applyButton),
                text = stringResource(id = R.string.apply)
            ) {

            }
        }
    }
}

@Composable
private fun OptionsContent(
    modifier: Modifier = Modifier,
    state: Settings.State,
    isOnboarding: Boolean,
    onEvent: (Settings.Event) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = Paddings.tiny)
            .verticalScroll(rememberScrollState())
            .padding(Paddings.default),
        verticalArrangement = Arrangement.spacedBy(Paddings.default)
    ) {
        SwitchOption(
            title = stringResource(id = R.string.use_mobile_data),
            checked = state.isMobileDataOn,
            onCheckedChange = {
                onEvent(Settings.Event.ToggleMobileData(it))
            }
        )

        SwitchOption(
            title = stringResource(id = R.string.set_mobile_limit),
            checked = state.isMobileDataLimitOn,
            onCheckedChange = {
                onEvent(Settings.Event.ToggleLimit(it))
            }
        ) {
            DataLimitInput(
                state = state,
                onEvent = onEvent
            )
        }


        if (!isOnboarding) {
            SwitchOption(
                title = stringResource(id = R.string.allow_use_on_battery),
                checked = state.isAllowUseOnBatteryOn,
                onCheckedChange = {
                    onEvent(Settings.Event.ToggleAllowUseOnBattery(it))
                }
            )

            ButtonOption(
                title = stringResource(id = R.string.shut_down),
                actionName = stringResource(id = R.string.shut_down)
            ) {
            }

            ButtonOption(
                title = stringResource(id = R.string.open),
                actionName = stringResource(id = R.string.open),
                color = Colors.textPrimary
            ) {
            }
        }
    }
}

@Composable
private fun DataLimitInput(
    state: Settings.State,
    onEvent: (Settings.Event) -> Unit
) {
    AnimatedVisibility(
        visible = state.isMobileDataLimitOn,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column(
            modifier = Modifier.padding(top = Paddings.default),
            verticalArrangement = Arrangement.spacedBy(Paddings.default)
        ) {
            InputTextField(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.mobile_data_limit),
                value = state.mobileDataLimit,
                onValueChange = {
                    onEvent(Settings.Event.UpdateLimit(it))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                error = if (state.mobileDataLimitInvalid) {
                    stringResource(
                        R.string.invalid_mobile_data_limit,
                        Config.minMobileDataLimit,
                        Config.maxMobileDataLimit
                    )
                } else {
                    null
                }
            )
            Text(
                text = stringResource(id = R.string.mobile_data_limit_reset),
                style = TextStyles.body,
                color = Colors.textSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    MysteriumTheme {
        SettingsContent(
            state = Settings.State(
                isMobileDataOn = true,
                isMobileDataLimitOn = true,
                isAllowUseOnBatteryOn = false,
                mobileDataLimit = "50",
                mobileDataLimitInvalid = true
            ),
            isOnboarding = false,
            onEvent = {},
            onNavigate = {}
        )
    }
}