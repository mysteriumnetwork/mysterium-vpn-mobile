package network.mysterium.provider.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.provider.Config
import network.mysterium.provider.R
import network.mysterium.provider.extensions.appVersion
import network.mysterium.provider.extensions.getActivity
import network.mysterium.provider.ui.components.buttons.BackButton
import network.mysterium.provider.ui.components.buttons.HelpButton
import network.mysterium.provider.ui.components.buttons.HomeButton
import network.mysterium.provider.ui.components.buttons.PrimaryButton
import network.mysterium.provider.ui.components.buttons.PrimaryTextButton
import network.mysterium.provider.ui.components.buttons.SecondaryButton
import network.mysterium.provider.ui.components.content.TitledScreenContent
import network.mysterium.provider.ui.components.input.InputTextField
import network.mysterium.provider.ui.components.progress.ProgressDialog
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.screens.settings.views.ButtonOption
import network.mysterium.provider.ui.screens.settings.views.SwitchOption
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.MysteriumTheme
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
import org.koin.androidx.compose.getViewModel
import kotlin.system.exitProcess

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    isOnboarding: Boolean,

    onNavigate: (NavigationDestination) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect("Settings") {
        viewModel.effect
            .collect {
                when (it) {
                    is Settings.Effect.Navigation -> onNavigate(it.destination)
                    Settings.Effect.CloseApp -> {
                        context.getActivity()?.finishAndRemoveTask()
                        exitProcess(0)
                    }
                }
            }
    }

    SettingsContent(
        state = state,
        isOnboarding = isOnboarding,
        appVersion = context.appVersion(),
        onEvent = { viewModel.setEvent(it) },
        onNavigate = onNavigate,
        onHelpPressed = { viewModel.trackHelpPressed() }
    )
}

@Composable
private fun SettingsContent(
    state: Settings.State,
    isOnboarding: Boolean,
    appVersion: String?,
    onEvent: (Settings.Event) -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    onHelpPressed: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    TitledScreenContent(
        title = stringResource(
            id = if (isOnboarding) {
                R.string.mobile_data_settings
            } else {
                R.string.settings
            }
        ),
        color = Colors.primaryBg,
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
        },
        navTrailing = {
            if (!isOnboarding) {
                HelpButton {
                    onHelpPressed()
                    uriHandler.openUri(Config.helpLink)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .background(color = Colors.primaryBg)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars),
        ) {
            OptionsContent(
                modifier = Modifier.weight(1f),
                state = state,
                isOnboarding = isOnboarding,
                appVersion = appVersion,
                onEvent = onEvent,
                onNavigate = onNavigate
            )

            if (isOnboarding) {
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Paddings.continueButton),
                    enabled = state.continueButtonEnabled,
                    text = stringResource(id = R.string.onboard_continue)
                ) {
                    onEvent(Settings.Event.OnContinue)
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (state.showShutDownConfirmation) {
        ConfirmShutDown(onEvent = onEvent)
    }

    if (state.isStartingNode) {
        ProgressDialog()
    }
}

@Composable
private fun OptionsContent(
    modifier: Modifier = Modifier,
    state: Settings.State,
    isOnboarding: Boolean,
    appVersion: String?,
    onEvent: (Settings.Event) -> Unit,
    onNavigate: (NavigationDestination) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = Paddings.tiny)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Paddings.default)
            .padding(top = Paddings.default),
        verticalArrangement = Arrangement.spacedBy(Paddings.default)
    ) {
        SwitchOption(
            title = stringResource(id = R.string.use_mobile_data),
            checked = state.isMobileDataOn,
            onCheckedChange = {
                onEvent(Settings.Event.ToggleMobileData(it))
            }
        )

        AnimatedVisibility(
            visible = state.isMobileDataOn,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
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
                title = stringResource(id = R.string.node_ui),
                subTitle = stringResource(id = R.string.open),
            ) {
                onNavigate(NavigationDestination.NodeUI())
            }

            ButtonOption(
                title = stringResource(id = R.string.shut_down),
                subTitle = stringResource(id = R.string.shut_down),
                painterResource = painterResource(id = R.drawable.ic_shut_down),
            ) {
                onEvent(Settings.Event.ShutDown)
            }
        }

        if (!isOnboarding && appVersion != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "v${appVersion}",
                style = TextStyles.label,
                color = Colors.grey500,
                textAlign = TextAlign.Center
            )
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
                info = "Mb",
                value = state.mobileDataLimit?.toString() ?: "",
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

            Row(
                modifier = Modifier.padding(top = Paddings.small),
                horizontalArrangement = Arrangement.spacedBy(Paddings.small)
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 28.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.mobile_data_limit_reset),
                    style = TextStyles.hint,
                    color = Colors.grey500
                )
                SecondaryButton(
                    text = stringResource(id = R.string.save),
                    enabled = state.isSaveButtonEnabled
                ) {
                    onEvent(Settings.Event.SaveMobileDataLimit)
                }
            }

        }
    }
}

@Composable
private fun ConfirmShutDown(
    onEvent: (Settings.Event) -> Unit
) {
    AlertDialog(
        title = {
            Text(
                text = stringResource(id = R.string.shut_down_node),
                style = TextStyles.header
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.shut_down_confirmation),
                style = TextStyles.body
            )
        },
        containerColor = Color.White,
        confirmButton = {
            PrimaryTextButton(
                text = stringResource(id = R.string.yes),
                color = Color.Red
            ) {
                onEvent(Settings.Event.ConfirmShutDown)
            }
        },
        dismissButton = {
            PrimaryTextButton(text = stringResource(id = R.string.cancel)) {
                onEvent(Settings.Event.CancelShutDown)
            }
        },
        onDismissRequest = {
            onEvent(Settings.Event.CancelShutDown)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    MysteriumTheme(LocalContext.current) {
        SettingsContent(
            state = Settings.State(
                isMobileDataOn = true,
                isMobileDataLimitOn = true,
                isAllowUseOnBatteryOn = false,
                mobileDataLimit = 50,
                mobileDataLimitInvalid = true,
                isSaveButtonEnabled = true,
                isStartingNode = false,
                showShutDownConfirmation = false,
                continueButtonEnabled = false,
                nodeError = null,
                isLoading = false,
            ),
            isOnboarding = false,
            appVersion = "1.0",
            onEvent = {},
            onNavigate = {},
            onHelpPressed = {},
        )
    }
}
