package network.mysterium.provider.ui.screens.nodeui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
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
            if (state.isRegistered) {
                HomeButton {
                    onNavigate(NavigationDestination.Home)
                }
            }
        },
        navTrailing = {
            if (state.isRegistered) {
                SettingsButton {
                    onNavigate(NavigationDestination.Settings())
                }
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
        state = NodeUI.State(
            url = "http://localhost:4449",
            isRegistered = false
        ),
        onNavigate = {}
    )
}