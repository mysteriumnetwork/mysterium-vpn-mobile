package network.mysterium.provider.ui.screens.nodeui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import network.mysterium.provider.ui.components.buttons.HomeButton
import network.mysterium.provider.ui.components.buttons.SettingsButton
import network.mysterium.provider.ui.components.content.LogoScreenContent
import network.mysterium.provider.ui.components.webview.ComposeWebView
import network.mysterium.provider.ui.navigation.NavigationDestination
import org.koin.androidx.compose.getViewModel

@Composable
fun NodeUIScreen(
    viewModel: NodeUIViewModel = getViewModel(),
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    NodeUIScreenContent(
        state = state,
        onNavigate = onNavigate
    )
}

@Composable
private fun NodeUIScreenContent(
    state: NodeUI.State,
    onNavigate: (NavigationDestination) -> Unit
) {
    LogoScreenContent(
        navLeading = {
            HomeButton {
                onNavigate(NavigationDestination.Home)
            }
        },
        navTrailing = {
            SettingsButton {
                onNavigate(NavigationDestination.Settings())
            }
        }
    ) {
        ComposeWebView(
            url = state.url
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun NodeUIScreenContentPreview() {
    NodeUIScreenContent(
        state = NodeUI.State("http://localhost:4449"),
        onNavigate = {}
    )
}