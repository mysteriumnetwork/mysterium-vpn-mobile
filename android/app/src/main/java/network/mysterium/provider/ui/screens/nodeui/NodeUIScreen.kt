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
import org.koin.core.parameter.parametersOf

@Composable
fun NodeUIScreen(
    authGrant: String? = null,
    viewModel: NodeUIViewModel = getViewModel(){ parametersOf(authGrant) },
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    NodeUIScreenContent(
        state = state,
        onEvent = {
            viewModel.setEvent(it)
        },
        onNavigate = onNavigate
    )
}

@Composable
private fun NodeUIScreenContent(
    state: NodeUI.State,
    onEvent: (NodeUI.Event) -> Unit,
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
            url = state.url,
            onReload = {
                onEvent(NodeUI.Event.SetReloadCallback(it))
            },
            onLoadUrl = {
                onEvent(NodeUI.Event.UrlLoaded(it))
            },
            onIgnoreCallback = {
                onEvent(NodeUI.Event.UrlLoaded(it, true))
            },
            ignoreList = state.ignoredUrls,
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun NodeUIScreenContentPreview() {
    NodeUIScreenContent(
        state = NodeUI.State(
            url = "http://localhost:4449",
            reload = {},
            isRegistered = false,
            ignoredUrls = emptyList(),
        ),
        onEvent = {},
        onNavigate = {}
    )
}
