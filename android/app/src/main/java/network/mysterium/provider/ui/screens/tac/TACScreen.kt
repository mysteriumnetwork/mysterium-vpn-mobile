package network.mysterium.provider.ui.screens.tac

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mysterium.node.model.NodeTerms
import network.mysterium.provider.R
import network.mysterium.provider.ui.components.content.LogoScreenContent
import network.mysterium.provider.ui.components.markdown.MarkdownText
import network.mysterium.provider.ui.navigation.NavigationDestination
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles
import org.koin.androidx.compose.getViewModel

@Composable
fun TACScreen(
    viewModel: TACViewModel = getViewModel(),
    onNavigation: (NavigationDestination) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    TACContent(
        state = state,
        onNavigation = onNavigation
    )
}

@Composable
private fun TACContent(
    state: TAC.State,
    onNavigation: (NavigationDestination) -> Unit
) {
    LogoScreenContent(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(Paddings.default),
        navLeading = {
            IconButton(
                onClick = {
                    onNavigation(NavigationDestination.PopBack)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Paddings.small)
            ) {
                Text(
                    text = stringResource(id = R.string.terms_and_conditions),
                    style = TextStyles.header,
                    color = Colors.blue700
                )
                Text(
                    text = state.terms.version,
                    style = TextStyles.body,
                    color = Colors.blue700
                )
            }
            MarkdownText(
                text = state.terms.content,
                textStyle = TextStyles.body3,
                color = Colors.grey500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsAndConditionsScreenPreview() {
    MaterialTheme {
        TACContent(state = TAC.State(NodeTerms("Terms content", "v1"))) {}
    }
}
