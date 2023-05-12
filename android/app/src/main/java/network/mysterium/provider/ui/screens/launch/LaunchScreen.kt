package network.mysterium.provider.ui.screens.launch

import android.app.Activity
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import network.mysterium.provider.R
import network.mysterium.provider.extensions.getActivity
import network.mysterium.provider.ui.components.buttons.PrimaryTextButton
import network.mysterium.provider.ui.components.logo.HeaderLogo
import network.mysterium.provider.ui.components.logo.HeaderLogoStyle
import network.mysterium.provider.ui.navigation.NavigationDestination
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
    val vpnLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.setEvent(Launch.Event.InitializeNode)
            } else {
                viewModel.setEvent(Launch.Event.DeclinedVpnPermission)
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
                        viewModel.setEvent(Launch.Event.InitializeNode)
                    }
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
            .background(Styles.background),
        contentAlignment = Alignment.Center
    ) {
        HeaderLogo(style = HeaderLogoStyle.Big)
    }

    state.error?.let {
        AlertDialog(
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
