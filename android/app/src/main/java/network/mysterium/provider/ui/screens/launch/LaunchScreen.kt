@file:OptIn(ExperimentalPermissionsApi::class)

package network.mysterium.provider.ui.screens.launch

import android.Manifest
import android.app.Activity
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import network.mysterium.provider.R
import network.mysterium.provider.extensions.getActivity
import network.mysterium.provider.ui.components.buttons.PrimaryTextButton
import network.mysterium.provider.ui.components.logo.HeaderLogo
import network.mysterium.provider.ui.components.logo.HeaderLogoStyle
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.Styles
import org.koin.androidx.compose.getViewModel

private const val LAUNCH_EFFECT = "launch_effect"

@Composable
fun LaunchScreen(
    viewModel: LaunchViewModel = getViewModel(),
    onNavigate: (NavigationDestination) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val batteryOptimization =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.setEvent(Launch.Event.InitializeNode)
        }

    val vpnLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    viewModel.setEvent(Launch.Event.RequestNotificationPermission)
                } else {
                    val pm: PowerManager? = context.getSystemService(POWER_SERVICE) as PowerManager?
                    if (pm?.isIgnoringBatteryOptimizations(context.packageName) == false) {
                        batteryOptimization.launch(
                            Intent()
                                .setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                .setData(Uri.parse("package:${context.packageName}"))
                        )
                    } else {
                        viewModel.setEvent(Launch.Event.InitializeNode)
                    }
                }
            } else {
                viewModel.setEvent(Launch.Event.DeclinedVpnPermission)
            }
        }

    val notificationPermissionRequest =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) {
            val pm: PowerManager? = context.getSystemService(POWER_SERVICE) as PowerManager?
            if (pm?.isIgnoringBatteryOptimizations(context.packageName) == false) {
                batteryOptimization.launch(
                    Intent()
                        .setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        .setData(Uri.parse("package:${context.packageName}"))
                )
            } else {
                viewModel.setEvent(Launch.Event.InitializeNode)
            }
        }

    LaunchedEffect(LAUNCH_EFFECT) {
        viewModel.effect.collect {
            when (it) {
                is Launch.Effect.Navigation -> {
                    onNavigate(it.destination)
                }

                Launch.Effect.RequestVpnPermission -> {
                    VpnService.prepare(context)?.let { intent ->
                        vpnLauncher.launch(intent)
                    } ?: run {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            viewModel.setEvent(Launch.Event.RequestNotificationPermission)
                        } else {
                            val pm: PowerManager? =
                                context.getSystemService(POWER_SERVICE) as PowerManager?
                            if (pm?.isIgnoringBatteryOptimizations(context.packageName) == false) {
                                batteryOptimization.launch(
                                    Intent()
                                        .setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                        .setData(Uri.parse("package:${context.packageName}"))
                                )
                            } else {
                                viewModel.setEvent(Launch.Event.InitializeNode)
                            }
                        }
                    }
                }

                Launch.Effect.RequestNotificationPermission -> {
                    notificationPermissionRequest.launchPermissionRequest()
                }

                Launch.Effect.CloseApp -> {
                    context.getActivity()?.finishAndRemoveTask()
                }
            }
        }
    }

    LaunchContent(state) {
        viewModel.setEvent(it)
    }
}

@Composable
private fun LaunchContent(
    state: Launch.State,
    onEvent: (Launch.Event) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(Styles.background),
        contentAlignment = Alignment.Center
    ) {
        HeaderLogo(
            modifier = Modifier.padding(bottom = Paddings.splashLogo),
            style = HeaderLogoStyle.Big,
        )
    }

    state.error?.let {
        AlertDialog(
            containerColor = Color.White,
            title = {
                Text(stringResource(id = R.string.app_name))
            },
            text = {
                Text(stringResource(id = it.messageResId))
            },
            confirmButton = {
                PrimaryTextButton(text = stringResource(id = R.string.ok)) {
                    onEvent(Launch.Event.ConfirmedInitError)
                }
            },
            onDismissRequest = {
                onEvent(Launch.Event.ConfirmedInitError)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LaunchScreenPreview() {
    LaunchScreen {}
}
