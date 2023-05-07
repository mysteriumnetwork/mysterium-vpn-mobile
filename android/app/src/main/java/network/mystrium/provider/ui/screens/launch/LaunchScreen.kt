package network.mystrium.provider.ui.screens.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import network.mystrium.provider.ui.components.logo.HeaderLogo
import network.mystrium.provider.ui.components.logo.HeaderLogoStyle
import network.mystrium.provider.ui.navigation.NavigationDestination
import network.mystrium.provider.ui.theme.Styles
import org.koin.androidx.compose.getViewModel

private const val LAUNCH_EFFECT = "launch_effect"

@Composable
fun LaunchScreen(
    viewModel: LaunchViewModel = getViewModel(),
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(LAUNCH_EFFECT) {
        viewModel.effect.collect {
            when (it) {
                is Launch.Effect.Navigation -> onNavigate(it.destination)
            }
        }
    }

    LaunchContent(
        state = state,
        onEvent = { viewModel.setEvent(it) }
    )
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
}

@Preview(showBackground = true)
@Composable
private fun LaunchScreenPreview() {
    LaunchScreen {}
}
