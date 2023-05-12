package network.mysterium.provider.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import network.mysterium.provider.Config
import network.mysterium.provider.R
import network.mysterium.provider.ui.components.buttons.BackButton
import network.mysterium.provider.ui.components.buttons.HomeButton
import network.mysterium.provider.ui.components.buttons.PrimaryButton
import network.mysterium.provider.ui.components.content.TitledScreenContent
import network.mysterium.provider.ui.components.input.InputTextField
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.screens.settings.views.ButtonOption
import network.mysterium.provider.ui.screens.settings.views.SwitchOption
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.MysteriumTheme
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
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
    TitledScreenContent(
        title = stringResource(id = R.string.mobile_data_settings),
        color = Colors.secondaryBg,
        navLeading = {
            if (isOnboarding) {
                BackButton {
                    onNavigate(NavigationDestination.PopBack)
                }
            } else {
                HomeButton {
                    onNavigate(NavigationDestination.Home)
                }
            }
        }
    ) {
        Column {
            OptionsContent(
                modifier = Modifier.weight(1f),
                state = state,
                isOnboarding = isOnboarding,
                onEvent = onEvent,
                onNavigate = onNavigate
            )

            if (isOnboarding) {
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Paddings.continueButton),
                    text = stringResource(id = R.string.onboard_continue)
                ) {
                    onNavigate(NavigationDestination.Home)
//                    onNavigate(NavigationDestination.NodeUI(true))
                }
            }
        }
    }
}

@Composable
private fun OptionsContent(
    modifier: Modifier = Modifier,
    state: Settings.State,
    isOnboarding: Boolean,
    onEvent: (Settings.Event) -> Unit,
    onNavigate: (NavigationDestination) -> Unit
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
                title = stringResource(id = R.string.open),
                actionName = stringResource(id = R.string.open),
                color = Colors.textPrimary
            ) {
                onNavigate(NavigationDestination.NodeUI())
            }

            ButtonOption(
                title = stringResource(id = R.string.shut_down),
                actionName = stringResource(id = R.string.shut_down)
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