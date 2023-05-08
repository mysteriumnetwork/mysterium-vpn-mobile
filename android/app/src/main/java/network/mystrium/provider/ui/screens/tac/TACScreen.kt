package network.mystrium.provider.ui.screens.tac

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import network.mystrium.provider.R
import network.mystrium.provider.ui.components.content.ScreenContent
import network.mystrium.provider.ui.components.markdown.MarkdownText
import network.mystrium.provider.ui.navigation.NavigationDestination
import network.mystrium.provider.ui.theme.Colors
import network.mystrium.provider.ui.theme.Paddings
import network.mystrium.provider.ui.theme.TextStyles
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
    ScreenContent(
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
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.terms_and_conditions),
                style = TextStyles.header,
                color = Colors.textPrimary,
                textAlign = TextAlign.Center
            )
            MarkdownText(
                text = state.terms,
                textStyle = TextStyles.body,
                color = Colors.textSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsAndConditionsScreenPreview() {
    MaterialTheme {
        TACContent(state = TAC.State("Terms content")) {}
    }
}
