package network.mysterium.provider.ui.screens.nodeui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import network.mysterium.provider.ui.components.buttons.HomeButton
import network.mysterium.provider.ui.components.buttons.SettingsButton
import network.mysterium.provider.ui.components.content.LogoScreenContent
import network.mysterium.provider.ui.components.webview.ComposeWebView
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.navigation.params.NodeUiParam
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NodeUIScreen(
    params: NodeUiParam? = null,
    viewModel: NodeUIViewModel = getViewModel { parametersOf(params) },
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.trackNodeUiOpened()
    }
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
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
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
            ignoreUrls = state.ignoredUrls
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
